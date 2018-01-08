package com.miskevich.webserver.server;


import com.miskevich.webserver.server.util.DirectoryScanner;
import com.miskevich.webserver.server.util.ServletContext;

public class ManuniaWebServer {

    private String webappPath;
    private int port;

    public ManuniaWebServer(String webappPath, int port) {
        this.webappPath = webappPath;
        this.port = port;
    }

    public void start() throws InterruptedException {
        ServletContext context = new ServletContext();
        DirectoryScanner directoryScanner = new DirectoryScanner(context, webappPath);

        Thread serverThread = new Thread(new ServerWorker(webappPath, port, context));
        Thread scannerThread = new Thread(new ScanWorker(directoryScanner));

        serverThread.start();
        scannerThread.start();
    }

    private static class ScanWorker implements Runnable {
        private DirectoryScanner directoryScanner;

        ScanWorker(DirectoryScanner directoryScanner) {
            this.directoryScanner = directoryScanner;
        }

        public void run() {
            directoryScanner.scanExistingWebapps();
            while (true) {
                directoryScanner.scanDirectoryForNewWars();
            }
        }
    }

    private static class ServerWorker implements Runnable {

        private String webappPath;
        private int port;
        private ServletContext context;

        ServerWorker(String webappPath, int port, ServletContext context) {
            this.webappPath = webappPath;
            this.port = port;
            this.context = context;
        }

        public void run() {
            Server server = new Server(port);
            server.setResourcePath(webappPath);
            server.setServletContext(context);
            server.run();
        }
    }
}
