package com.miskevich.webserver.server.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DirectoryScanner {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private static final String EXTENSION = "war";

    private WatchService watcher;
    private ThreadPoolExecutor executor;
    private ServletContext context;
    private Path directory;

    public DirectoryScanner(ServletContext context, String path) {
        try {
            Path directory = Paths.get(path);
            this.directory = directory;
            this.context = context;

            this.watcher = directory.getFileSystem().newWatchService();
            directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

            executor = new ThreadPoolExecutor(5, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        } catch (IOException e) {
            LOG.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    public void scanDirectoryForNewWars() {
        String warFileName;
        try {
            WatchKey watchKey = watcher.poll(1, TimeUnit.MINUTES);
            if (null != watchKey) {
                List<WatchEvent<?>> events = watchKey.pollEvents();
                Path dir = (Path) watchKey.watchable();
                for (WatchEvent event : events) {
                    Path fullPath = dir.resolve(String.valueOf(event.context()));
                    warFileName = event.context().toString();
                    String extension = FilenameUtils.getExtension(warFileName);

                    if (EXTENSION.equalsIgnoreCase(extension)) {
                        LOG.info("Found new war file: " + warFileName);

                        ServletContextMaker servletContextMaker = new ServletContextMaker(fullPath.toString(), context);
                        executor.execute(servletContextMaker);
                    }
                }
                watchKey.reset();
            }
        } catch (InterruptedException e) {
            LOG.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    public void scanExistingWebapps() {
        File[] files = directory.toFile().listFiles(x -> x.getName().endsWith(EXTENSION));
        if (files != null) {
            for (File file : files) {
                LOG.info("Found existing war file: " + file.getAbsolutePath());

                ServletContextMaker servletContextMaker = new ServletContextMaker(file.getAbsolutePath(), context);
                executor.execute(servletContextMaker);
            }
        }
    }
}
