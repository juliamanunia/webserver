package com.miskevich.webserver.model;

public enum HttpMethod {
    GET("GET");

    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod getMethodById(String method) {
        for (HttpMethod httpMethod : values()) {
            if(httpMethod.method.equalsIgnoreCase(method)){
                return httpMethod;
            }
        }
        throw new IllegalArgumentException("Method " + method + " is not supported");
    }
}
