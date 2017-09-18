package com.miskevich.webserver.server.util;

import com.miskevich.webserver.model.ServletResponse;
import com.miskevich.webserver.model.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseHeaderGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseHeaderGenerator.class);
    private static final String OK = "HTTP/1.1 200 OK";
    private static final String NOT_FOUND = "HTTP/1.1 404 Not found";

    public static String generateHeadersForServlet(ServletResponse servletResponse, boolean error) {
        StringBuilder responseHeaders = new StringBuilder(error ? NOT_FOUND : OK);
        responseHeaders.append("\n");

        if (null != servletResponse.getContentType()) {
            responseHeaders
                    .append("Content-Type: ")
                    .append(servletResponse.getContentType())
                    .append("\n");
        }
        if (servletResponse.getContentLengthLong() > 0) {
            responseHeaders
                    .append("Content-Length: ")
                    .append(servletResponse.getContentLengthLong())
                    .append("\n");
        }

        injectStandardHeaders(responseHeaders);
        responseHeaders.append("\n");

        LOG.info("Headers for servlet were generated");
        return responseHeaders.toString();
    }

    public static String generateHeadersForStaticResources(Resource resource, boolean error) {
        StringBuilder responseHeaders = new StringBuilder(error ? NOT_FOUND : OK);
        responseHeaders.append("\n");
        injectHeaders(responseHeaders, resource);
        injectStandardHeaders(responseHeaders);
        responseHeaders.append("\n");

        LOG.info("Headers for static resource were generated");
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

    private static void injectStandardHeaders(StringBuilder responseHeaders) {
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
