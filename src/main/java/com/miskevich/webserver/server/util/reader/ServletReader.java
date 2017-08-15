package com.miskevich.webserver.server.util.reader;

import com.miskevich.webserver.model.ServletDefinition;

import java.util.List;

public interface ServletReader {
    List<ServletDefinition> getServlets(String path);
}
