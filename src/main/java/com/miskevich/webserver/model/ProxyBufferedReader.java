package com.miskevich.webserver.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class ProxyBufferedReader extends BufferedReader {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public ProxyBufferedReader(Reader in) {
        super(in);
    }

    @Override
    public void close() throws IOException {
        LOG.info("Socket was closed from outside");
    }
}
