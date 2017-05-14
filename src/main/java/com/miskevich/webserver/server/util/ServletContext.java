package com.miskevich.webserver.server.util;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

public class ServletContext {

    private Map<String, HttpServlet> servletHolder;

    public ServletContext() {
        this.servletHolder = new HashMap<>();
    }

    public void addServlet(String path, HttpServlet servlet){
        servletHolder.put(path, servlet);
    }

    public HttpServlet getServlet(String path){
        return servletHolder.get(path);
    }
}
