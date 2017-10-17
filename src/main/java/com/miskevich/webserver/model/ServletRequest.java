package com.miskevich.webserver.model;

import com.miskevich.webserver.model.adapter.HttpServletRequestAdapter;
import com.miskevich.webserver.model.common.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletRequest extends HttpServletRequestAdapter {

    private HttpMethod method;
    private Map<String, String[]> requestParameters = new HashMap<>();
    private BufferedReader reader;


    @Override
    public String getMethod() {
        return method.getMethod();
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return requestParameters;
    }

    public void addParameter(String name, String[] value) {
        requestParameters.put(name, value);
    }

    @Override
    public String[] getParameterValues(String name) {
        return requestParameters.get(name);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(reader);
        //return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

}
