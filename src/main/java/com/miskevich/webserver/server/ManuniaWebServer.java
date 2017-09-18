package com.miskevich.webserver.server;


import com.miskevich.webserver.server.util.DirectoryScanner;
import com.miskevich.webserver.server.util.ServletContext;
import com.miskevich.webserver.server.util.ServletContextMaker;

public class ManuniaWebServer {

    public static void main(String[] args) throws InterruptedException {
        ServletContext context = new ServletContext();
        DirectoryScanner directoryScanner = new DirectoryScanner(context);

        Thread serverThread = new Thread(new ServerWorker(context));
        Thread scannerThread = new Thread(new ScanWorker(directoryScanner));

        serverThread.start();
        scannerThread.start();
        // TODO: run existing webapps after start of container
        ServletContextMaker alreadyUnzippedContext = new ServletContextMaker(context);
        alreadyUnzippedContext.initializeExistingContext();
    }

    private static class ScanWorker implements Runnable {
        private DirectoryScanner directoryScanner;

        ScanWorker(DirectoryScanner directoryScanner) {
            this.directoryScanner = directoryScanner;
        }

        public void run() {
            while (true) {
                directoryScanner.scanDirectoryForNewWars();
            }
        }
    }

    private static class ServerWorker implements Runnable {
        private ServletContext context;

        ServerWorker(ServletContext context) {
            this.context = context;
        }

        public void run() {
            Server server = new Server(3000);
            server.setResourcePath("src/main/webapp");
            server.setServletContext(context);
            server.run();
        }
    }
}
