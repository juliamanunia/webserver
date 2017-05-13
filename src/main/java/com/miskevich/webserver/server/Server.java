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
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
                RequestHandler requestHandler = new RequestHandler(reader, writer, resourceReader);
                new Thread(requestHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
