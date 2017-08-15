package com.miskevich.webserver.model;

import java.util.List;

public class ServletDefinition {
    private String name;
    private String className;
    private List<String> urls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServletDefinition that = (ServletDefinition) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        return urls != null ? urls.equals(that.urls) : that.urls == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (urls != null ? urls.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServletDefinition{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", urls=" + urls +
                '}';
    }
}
