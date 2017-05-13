package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPooledServer implements Runnable {

    private int port;
    private ResourceReader resourceReader;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public ThreadPooledServer(int port) {
        this.port = port;
    }

    public void setResourcePath(String path){
        resourceReader = new ResourceReader(path);
    }

    @Override
    public void run() {
        try {
            ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) threadPool;
            poolExecutor.setCorePoolSize(5);
            poolExecutor.setMaximumPoolSize(20);

            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
                RequestHandler requestHandler = new RequestHandler(reader, writer, resourceReader);
                poolExecutor.execute(requestHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


