package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;
    private ResourceReader resourceReader;

    public Server(int port) {
        this.port = port;
    }

    public void setResourcePath(String path){
        resourceReader = new ResourceReader(path);
    }

    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                new Thread(new WorkerRunnable(socket, resourceReader)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
