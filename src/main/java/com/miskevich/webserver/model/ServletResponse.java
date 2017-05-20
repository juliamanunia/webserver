package com.miskevich.webserver.model;

import com.miskevich.webserver.model.adapter.HttpServletResponseAdapter;
import com.miskevich.webserver.model.common.HttpStatus;

import java.io.*;

public class ServletResponse extends HttpServletResponseAdapter {

    private ByteArrayOutputStream contentStream;
    private PrintWriter printWriter;
    private HttpStatus status;
    private String contentType;
    private long contentLength;

    public ServletResponse() {
        this.contentStream = new ByteArrayOutputStream();
    }


    @Override
    public PrintWriter getWriter() throws IOException {
        if (printWriter == null) {
            OutputStreamWriter streamWriter = new OutputStreamWriter(contentStream);
            printWriter = new PrintWriter(streamWriter);
        }

        return printWriter;
    }

    public ByteArrayOutputStream getContent(){
        return contentStream;
    }

    @Override
    public int getStatus() {
        return status.getStatusCode();
    }

    @Override
    public void setStatus(int statusCode) {
        this.status = HttpStatus.getStatusByCode(statusCode);
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLengthLong() {
        return contentLength;
    }

    @Override
    public void setContentLengthLong(long contentLength) {
        this.contentLength = contentLength;
    }

}
