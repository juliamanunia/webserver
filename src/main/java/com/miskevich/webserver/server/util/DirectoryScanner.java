package com.miskevich.webserver.server.util;

import com.miskevich.webserver.server.util.reader.XMLServletReader;
import com.miskevich.webserver.server.util.zip.Unzipper;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DirectoryScanner {

    private WatchService watcher;
    private static final String EXTENSION = "war";

    public DirectoryScanner() {
        try {
            Path directory = Paths.get("src", "main", "webapp");
            this.watcher = directory.getFileSystem().newWatchService();
            directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
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
                        System.out.println("Found new war file: " + warFileName);

                        Unzipper unzipper = new Unzipper(warFileName);
                        unzipper.unzip();
                        String webXml = unzipper.findWebXml();
                        //TODO: if webXml wasn't found log an error, and don't parse it

                        XMLServletReader xmlServletReader = new XMLServletReader();
                        xmlServletReader.getServlets(webXml);
                    }
                }
                watchKey.reset();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
