package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;
import com.miskevich.webserver.model.Request;

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
            request.setUrl(staticResourceRequest.getUrl());
            request.setMethod(staticResourceRequest.getMethod());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Response response = new Response(byteArrayOutputStream);
            HttpServlet servlet = servletContext.getServlet(request.getUrl());

            if(null != servlet){
                servlet.service(request, response);
                if(200 == response.getStatus()){
                    writer.write(ResponseHeaderGenerator.generate(false).getBytes());

                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    writer.write(bytes);
                }else {
                    writer.write(ResponseHeaderGenerator.generate(true).getBytes());
                }
            }else {
                //Load static resources
                BufferedInputStream resource = resourceReader.getResource(staticResourceRequest.getUrl());
                if(resource.available() != 0){
                    writer.write(ResponseHeaderGenerator.generate(false).getBytes());
                    byte[] buffer = new byte[1024 * 8 * 30];
                    int length;
                    while ((length = resource.read(buffer)) != -1){
                        writer.write(buffer, 0, length);
                    }
                }else {
                    writer.write(ResponseHeaderGenerator.generate(true).getBytes());
                }
            }
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        handle();
    }
}
