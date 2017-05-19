package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;
import com.miskevich.webserver.model.Request;
import com.miskevich.webserver.model.Resource;
import com.miskevich.webserver.model.Response;
import com.miskevich.webserver.model.StaticResourceRequest;
import com.miskevich.webserver.server.util.RequestParser;
import com.miskevich.webserver.server.util.ResponseHeaderGenerator;
import com.miskevich.webserver.server.util.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.*;

public class RequestHandler implements Runnable{

    private BufferedReader reader;
    private BufferedOutputStream writer;
    private ResourceReader resourceReader;
    private ServletContext servletContext;

    public RequestHandler(BufferedReader reader, BufferedOutputStream writer, ResourceReader resourceReader, ServletContext servletContext) {
        this.reader = reader;
        this.writer = writer;
        this.resourceReader = resourceReader;
        this.servletContext = servletContext;
    }

    public void handle() {
        try (BufferedOutputStream writer = this.writer){

            StaticResourceRequest staticResourceRequest = RequestParser.toRequest(reader);
            Request request = new Request();
            request.setMethod(staticResourceRequest.getMethod());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Response response = new Response(byteArrayOutputStream);
            HttpServlet servlet = servletContext.getServlet(staticResourceRequest.getUrl());

            if(null != servlet){
                loadServletResources(writer, request, byteArrayOutputStream, response, servlet);
            }else {
                loadStaticResources(writer, staticResourceRequest);
            }
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadServletResources(BufferedOutputStream writer, Request request, ByteArrayOutputStream byteArrayOutputStream,
                                      Response response, HttpServlet servlet) throws ServletException, IOException {

        servlet.service(request, response);

        if(200 == response.getStatus()){
            writer.write(ResponseHeaderGenerator.generateHeadersForServlet(response, false).getBytes());
            byte[] bytes = byteArrayOutputStream.toByteArray();
            writer.write(bytes);
        }else {
            writer.write(ResponseHeaderGenerator.generateHeadersForServlet(response, true).getBytes());
        }
    }

    private void loadStaticResources(BufferedOutputStream writer, StaticResourceRequest staticResourceRequest) {
        Resource resource = resourceReader.getResource(staticResourceRequest.getUrl());
        try(BufferedInputStream content = resource.getContent()){

            if(content.available() != 0){
                writer.write(ResponseHeaderGenerator.generateHeadersForStaticResources(resource, false).getBytes());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = content.read(buffer)) != -1){
                    writer.write(buffer, 0, length);
                }
            }else {
                writer.write(ResponseHeaderGenerator.generateHeadersForStaticResources(resource, true).getBytes());
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        handle();
    }
}
