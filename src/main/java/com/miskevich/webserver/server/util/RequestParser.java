package com.miskevich.webserver.server.util;

import com.miskevich.webserver.model.ServletRequest;
import com.miskevich.webserver.model.common.HttpMethod;
import com.miskevich.webserver.model.resources.StaticResourceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class RequestParser {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParser.class);

    public static StaticResourceRequest toRequest(BufferedReader reader, InputStream inputStream, ServletRequest servletRequest) {
        StaticResourceRequest staticResourceRequest = new StaticResourceRequest();
        try {
            injectUrlAndMethod(staticResourceRequest, reader.readLine());
            injectHeaders(staticResourceRequest, reader);
            LOG.info("Parsed request " + staticResourceRequest);
            if (!staticResourceRequest.getMethod().equals(HttpMethod.GET)) {
                //int contentLength = Integer.valueOf(staticResourceRequest.getHeaders().get("Content-Length").trim());
                //char[] body = new char[contentLength * 5];
                //reader.read(body, 0, contentLength);
                //System.out.println(body);
                //servletRequest.setInputStream(inputStream);
                servletRequest.setReader(reader);
            }
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

    private static void injectBody() {
        //TODO: POST, PUT, DELETE --> read empty string and then parameters
    }
}
