package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;
import com.miskevich.webserver.model.Request;
import com.miskevich.webserver.server.util.RequestParser;
import com.miskevich.webserver.server.util.ResponseHeaderGenerator;

import java.io.*;

public class RequestHandler {

    private BufferedReader reader;
    private BufferedOutputStream writer;
    private ResourceReader resourceReader;

    public RequestHandler(BufferedReader reader, BufferedOutputStream writer, ResourceReader resourceReader) {
        this.reader = reader;
        this.writer = writer;
        this.resourceReader = resourceReader;
    }

    public void handle() {
        try (BufferedOutputStream writer = this.writer){
            Request request = RequestParser.toRequest(reader);
            BufferedInputStream resource = resourceReader.getResource(request.getUrl());

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
