package com.miskevich.webserver.server;


import com.miskevich.webserver.server.util.DirectoryScanner;
import com.miskevich.webserver.server.util.ServletContext;

public class ManuniaWebServer {

    public static void main(String[] args) throws InterruptedException {
        ServletContext context = new ServletContext();
        DirectoryScanner directoryScanner = new DirectoryScanner(context);

        Thread server = new Thread(new ServerWorker(context));
        Thread scanner = new Thread(new ScanWorker(directoryScanner));

        server.start();
        scanner.start();

        server.join();
        scanner.join();
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
