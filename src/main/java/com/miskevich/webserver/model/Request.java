package com.miskevich.webserver.model;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private String url;
    private HttpMethod method;
    private Map<String, String> headers = new HashMap<String, String>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value){
        headers.put(key, value);
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method=" + method +
                ", headers=" + headers +
                '}';
    }
}
