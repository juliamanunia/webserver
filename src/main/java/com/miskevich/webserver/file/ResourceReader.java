package com.miskevich.webserver.file;

import com.miskevich.webserver.model.Resource;

import java.io.*;
import java.nio.file.Files;

public class ResourceReader {

    private String path;

    public ResourceReader(String path) {
        this.path = path;
    }

    public Resource getResource(String url) {
        File pathToResource = new File(path + File.separator + url);
        Resource resource = new Resource();
        resource.setResourceLocalPath(pathToResource);

        try {
            resource.setContentType(Files.probeContentType(pathToResource.toPath()));
            resource.setContentLength(Files.size(pathToResource.toPath()));
            resource.setContent(new BufferedInputStream(new FileInputStream(pathToResource)));
            return resource;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
