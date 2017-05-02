package com.miskevich.webserver.server.util;

import com.miskevich.webserver.model.HttpMethod;
import com.miskevich.webserver.model.Request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestParser {

    public static Request toRequest(BufferedReader reader){
        Request request = new Request();
        try {
            injectUrlAndMethod(request, reader.readLine());
            injectHeaders(request, reader);
            System.out.println("Parsed request " + request);
            return request;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void injectUrlAndMethod(Request request, String line){
        String[] methodAndUrl = line.split(" ");
        request.setMethod(HttpMethod.getMethodById(methodAndUrl[0]));
        request.setUrl(methodAndUrl[1]);
    }

    private static void injectHeaders(Request request, BufferedReader reader) throws IOException {
        String parameter;
        while (reader.ready() && !(parameter = reader.readLine()).isEmpty()){
            String[] nameAndValue = parameter.split(":");
            request.addHeader(nameAndValue[0], nameAndValue[1]);
        }
    }

}
