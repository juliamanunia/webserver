package com.miskevich.webserver.model.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum HttpMethod {

    GET("GET"), POST("POST");

    private String method;
    private static final Logger LOG = LoggerFactory.getLogger(HttpMethod.class);

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static HttpMethod getMethodById(String method) {
        for (HttpMethod httpMethod : values()) {
            if (httpMethod.method.equalsIgnoreCase(method)) {
                return httpMethod;
            }
        }
        String message = "Method " + method + " is not supported";
        LOG.warn(message);
        throw new IllegalArgumentException(message);
    }
}
