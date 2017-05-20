package com.miskevich.webserver.model.resources;

import java.io.BufferedInputStream;
import java.io.File;

public class Resource {

    private File resourceLocalPath;
    private BufferedInputStream content;
    private String contentType;
    private long contentLength;

    public File getResourceLocalPath() {
        return resourceLocalPath;
    }

    public void setResourceLocalPath(File resourceLocalPath) {
        this.resourceLocalPath = resourceLocalPath;
    }

    public BufferedInputStream getContent() {
        return content;
    }

    public void setContent(BufferedInputStream content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceLocalPath=" + resourceLocalPath.getAbsolutePath() +
                '}';
    }
}
