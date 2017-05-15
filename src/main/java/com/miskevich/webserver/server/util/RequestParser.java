package com.miskevich.webserver.server.util;

import com.miskevich.webserver.model.HttpMethod;
import com.miskevich.webserver.model.StaticResourceRequest;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestParser {

    public static StaticResourceRequest toRequest(BufferedReader reader){
        StaticResourceRequest staticResourceRequest = new StaticResourceRequest();
        try {
            injectUrlAndMethod(staticResourceRequest, reader.readLine());
            injectHeaders(staticResourceRequest, reader);
            System.out.println("Parsed request " + staticResourceRequest);
            return staticResourceRequest;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void injectUrlAndMethod(StaticResourceRequest staticResourceRequest, String line){
        String[] methodAndUrl = line.split(" ");
        staticResourceRequest.setMethod(HttpMethod.getMethodById(methodAndUrl[0]));
        staticResourceRequest.setUrl(methodAndUrl[1]);
    }

    private static void injectHeaders(StaticResourceRequest staticResourceRequest, BufferedReader reader) throws IOException {
        String parameter;
        while (reader.ready() && !(parameter = reader.readLine()).isEmpty()){
            String[] nameAndValue = parameter.split(":");
            staticResourceRequest.addHeader(nameAndValue[0], nameAndValue[1]);
        }
    }

}
