package com.miskevich.webserver.server.util;

import com.miskevich.webserver.model.common.HttpMethod;
import com.miskevich.webserver.model.resources.StaticResourceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestParser {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParser.class);

    public static StaticResourceRequest toRequest(BufferedReader reader) {
        StaticResourceRequest staticResourceRequest = new StaticResourceRequest();
        try {
            injectUrlAndMethod(staticResourceRequest, reader.readLine());
            injectHeaders(staticResourceRequest, reader);
            LOG.info("Parsed request " + staticResourceRequest);
            return staticResourceRequest;
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void injectUrlAndMethod(StaticResourceRequest staticResourceRequest, String line) {
        System.out.println("line: " + line);
        String[] methodAndUrl = line.split(" ");
        staticResourceRequest.setMethod(HttpMethod.getMethodById(methodAndUrl[0]));
        staticResourceRequest.setUrl(methodAndUrl[1]);
    }

    private static void injectHeaders(StaticResourceRequest staticResourceRequest, BufferedReader reader) throws IOException {
        String parameter;
        while (reader.ready() && !(parameter = reader.readLine()).isEmpty()) {
            String[] nameAndValue = parameter.split(":");
            staticResourceRequest.addHeader(nameAndValue[0], nameAndValue[1]);
        }
    }

    private static void injectBody(){
        //TODO: POST, PUT, DELETE --> read empty string and then parameters
    }
}
