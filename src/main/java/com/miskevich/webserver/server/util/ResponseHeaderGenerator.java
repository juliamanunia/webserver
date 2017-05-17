package com.miskevich.webserver.server.util;

import com.miskevich.webserver.model.Resource;
import com.miskevich.webserver.model.Response;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseHeaderGenerator {
    private static final String OK = "HTTP/1.1 200 OK";
    private static final String NOT_FOUND = "HTTP/1.1 404 Not found";

    public static String generateHeadersForServlet(Response response, boolean error){
        StringBuilder responseHeaders = new StringBuilder(error ? NOT_FOUND : OK);
        responseHeaders.append("\n");

        if(null != response.getContentType()){
            responseHeaders
                    .append("Content-Type: ")
                    .append(response.getContentType())
                    .append("\n");
        }
        if(response.getContentLengthLong() > 0){
            responseHeaders
                    .append("Content-Length: ")
                    .append(response.getContentLengthLong())
                    .append("\n");
        }

        injectStandardHeaders(responseHeaders);
        responseHeaders.append("\n");

        return responseHeaders.toString();
    }

    public static String generateHeadersForStaticResources(Resource resource, boolean error) {
        StringBuilder responseHeaders = new StringBuilder(error ? NOT_FOUND : OK);
        responseHeaders.append("\n");
        injectHeaders(responseHeaders, resource);
        injectStandardHeaders(responseHeaders);
        responseHeaders.append("\n");

        return responseHeaders.toString();
    }

    private static void injectHeaders(StringBuilder responseHeaders, Resource resource) {
        responseHeaders
                .append("Content-Type: ")
                .append(resource.getContentType())
                .append("\n")
                .append("Content-Length: ")
                .append(resource.getContentLength())
                .append("\n");
    }

    private static void injectStandardHeaders(StringBuilder responseHeaders){
        responseHeaders
                .append("Date: ")
                .append(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT"))))
                .append("\n")
                .append("Server: ")
                .append("Miskevich/0.0.1")
                .append("\n")
                .append("Cache-Control: ")
                .append("max-age=3600")
                .append("\n");
    }
}
