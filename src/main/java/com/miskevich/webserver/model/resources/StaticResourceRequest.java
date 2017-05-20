package com.miskevich.webserver.model.resources;

import com.miskevich.webserver.model.common.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class StaticResourceRequest {

    private String url;
    private HttpMethod method;
    private Map<String, String> headers = new HashMap<>();

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaticResourceRequest that = (StaticResourceRequest) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (method != that.method) return false;
        return headers != null ? headers.equals(that.headers) : that.headers == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StaticRequest{" +
                "url='" + url + '\'' +
                ", method=" + method +
                ", headers=" + headers +
                '}';
    }
}
