package com.miskevich.webserver.model;

import com.miskevich.webserver.model.adapter.HttpServletRequestAdapter;
import com.miskevich.webserver.model.common.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
        return new ProxyBufferedReader(reader);
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

}

class ProxyBufferedReader extends BufferedReader{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public ProxyBufferedReader(Reader in) {
        super(in);
    }

    @Override
    public void close() throws IOException {
        LOG.info("Socket was closed from outside");
    }
}
