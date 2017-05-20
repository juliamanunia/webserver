package com.miskevich.webserver.file;

import com.miskevich.webserver.model.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;

public class ResourceReader {

    private String path;
    private static final Logger LOG = LoggerFactory.getLogger(ResourceReader.class);

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
            LOG.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
