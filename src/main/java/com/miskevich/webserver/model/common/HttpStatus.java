package com.miskevich.webserver.model.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not found");

    private int statusCode;
    private String statusText;
    private static final Logger LOG = LoggerFactory.getLogger(HttpStatus.class);

    HttpStatus(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    @Override
    public String toString() {
        return "HttpStatus{" +
                "statusCode=" + statusCode +
                ", statusText='" + statusText + '\'' +
                '}';
    }

    public static HttpStatus getStatusByCode(int statusCode) {
        for (HttpStatus httpStatus : values()) {
            if (httpStatus.statusCode == statusCode) {
                return httpStatus;
            }
        }
        String message = "No status with id " + statusCode + " was found!";
        LOG.warn(message);
        throw new IllegalArgumentException(message);
    }
}
