package com.miskevich.webserver.server.util;

public class ResponseHeaderGenerator {
    private static final String OK = "HTTP/1.1 200 OK";
    private static final String NOT_FOUND = "HTTP/1.1 404 Not found";

    public static String generate(boolean error) {
        return error ? NOT_FOUND : OK + "\n\n";
    }
}
