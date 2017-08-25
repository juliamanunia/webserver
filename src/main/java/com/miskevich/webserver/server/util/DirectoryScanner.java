package com.miskevich.webserver.server.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DirectoryScanner {

    private WatchService watcher;
    private static final String EXTENSION = "war";
    private ThreadPoolExecutor executor;
    private ServletContext context;
    private static final Logger LOG = LoggerFactory.getLogger(DirectoryScanner.class);

    public DirectoryScanner(ServletContext context) {
        try {
            Path directory = Paths.get("src", "main", "webapp");
            this.watcher = directory.getFileSystem().newWatchService();
            directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            this.context = context;
            executor = new ThreadPoolExecutor(5, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void scanDirectoryForNewWars() {
        String warFileName;
        try {
            WatchKey watchKey = watcher.poll(1, TimeUnit.MINUTES);
            if (null != watchKey) {
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent event : events) {
                    warFileName = event.context().toString();
                    String extension = FilenameUtils.getExtension(warFileName);

                    if (EXTENSION.equalsIgnoreCase(extension)) {
                        LOG.info("Found new war file: " + warFileName);

                        ServletContextMaker servletContextMaker = new ServletContextMaker(warFileName, context);
                        executor.execute(servletContextMaker);
                    }
                }
                watchKey.reset();
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
