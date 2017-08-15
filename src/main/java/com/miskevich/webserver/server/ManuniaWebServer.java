package com.miskevich.webserver.server;


import com.miskevich.webserver.server.util.DirectoryScanner;

public class ManuniaWebServer {

    public static void main(String[] args) {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        while (true) {
            directoryScanner.scanDirectoryForNewWars();
        }
    }
}
