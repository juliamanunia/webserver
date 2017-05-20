package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;
import com.miskevich.webserver.model.ServletRequest;
import com.miskevich.webserver.model.resources.Resource;
import com.miskevich.webserver.model.ServletResponse;
import com.miskevich.webserver.model.resources.StaticResourceRequest;
import com.miskevich.webserver.server.util.RequestParser;
import com.miskevich.webserver.server.util.ResponseHeaderGenerator;
import com.miskevich.webserver.server.util.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable{

    private BufferedReader reader;
    private BufferedOutputStream writer;
    private ResourceReader resourceReader;
    private ServletContext servletContext;
    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    public RequestHandler(BufferedReader reader, BufferedOutputStream writer, ResourceReader resourceReader, ServletContext servletContext) {
        this.reader = reader;
        this.writer = writer;
        this.resourceReader = resourceReader;
        this.servletContext = servletContext;
    }

    public void handle() {
        try (BufferedOutputStream writer = this.writer){

            StaticResourceRequest staticResourceRequest = RequestParser.toRequest(reader);
            ServletRequest servletRequest = new ServletRequest();
            servletRequest.setMethod(staticResourceRequest.getMethod());

            ServletResponse servletResponse = new ServletResponse();
            HttpServlet servlet = servletContext.getServlet(staticResourceRequest.getUrl());

            if(null != servlet){
                processServlet(writer, servletRequest, servletResponse, servlet);
            }else {
                loadStaticResources(writer, staticResourceRequest);
            }
        } catch (IOException | ServletException e) {
            LOG.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void processServlet(BufferedOutputStream socketOutputStream, ServletRequest servletRequest,
                                ServletResponse servletResponse, HttpServlet servlet) throws ServletException, IOException {

        //processing
        servlet.service(servletRequest, servletResponse);

        if(200 == servletResponse.getStatus()){
            //response header
            String responseHeaders = ResponseHeaderGenerator.generateHeadersForServlet(servletResponse, false);
            socketOutputStream.write(responseHeaders.getBytes());

            //response body
            byte[] bytes = servletResponse.getContent().toByteArray();
            socketOutputStream.write(bytes);
        }else {
            socketOutputStream.write(ResponseHeaderGenerator.generateHeadersForServlet(servletResponse, true).getBytes());
        }
    }

    private void loadStaticResources(BufferedOutputStream writer, StaticResourceRequest staticResourceRequest) {
        Resource resource = resourceReader.getResource(staticResourceRequest.getUrl());
        try(BufferedInputStream content = resource.getContent()){

            if(content.available() != 0){
                //response header
                String headersForStaticResources = ResponseHeaderGenerator.generateHeadersForStaticResources(resource, false);
                writer.write(headersForStaticResources.getBytes());

                //response body
                //buffer size is 128 Kb, based on performance test results:
                // CPU on Windows is reached to 100% with 256 Kb buffer
                byte[] buffer = new byte[1024 * 128];
                int length;
                while ((length = content.read(buffer)) != -1){
                    writer.write(buffer, 0, length);
                }
            }else {
                writer.write(ResponseHeaderGenerator.generateHeadersForStaticResources(resource, true).getBytes());
            }
        }catch (IOException e){
            LOG.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        handle();
    }
}
