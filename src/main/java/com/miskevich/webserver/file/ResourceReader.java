package com.miskevich.webserver.file;

import java.io.*;

public class ResourceReader {

    private String path;

    public ResourceReader(String path) {
        this.path = path;
    }

    public BufferedInputStream getResource(String url) {
        File pathToResource = new File(path + File.separator + url);
        try {
            return new BufferedInputStream(new FileInputStream(pathToResource));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
