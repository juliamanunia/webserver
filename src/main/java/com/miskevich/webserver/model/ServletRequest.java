package com.miskevich.webserver.model;

import com.miskevich.webserver.model.adapter.HttpServletRequestAdapter;
import com.miskevich.webserver.model.common.HttpMethod;

public class ServletRequest extends HttpServletRequestAdapter {

    private HttpMethod method;


    @Override
    public String getMethod() {
        return method.getMethod();
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

}
