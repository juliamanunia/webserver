package com.miskevich.webserver.model.common;

public enum WebXmlTags {

    SERVLET("servlet"),
    SERVLET_NAME("servlet-name"),
    SERVLET_CLASS("servlet-class"),
    SERVLET_MAPPING("servlet-mapping"),
    URL_PATTERN("url-pattern");

    private String tagName;

    WebXmlTags(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }
}
