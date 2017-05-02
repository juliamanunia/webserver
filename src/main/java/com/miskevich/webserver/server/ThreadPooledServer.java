package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPooledServer implements Runnable {

    private int port;
    private ResourceReader resourceReader;
    private Thread runningThread;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public ThreadPooledServer(int port) {
        this.port = port;
    }

    public void setResourcePath(String path){
        resourceReader = new ResourceReader(path);
    }

    @Override
    public void run() {
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                threadPool.execute(new WorkerRunnable(socket, resourceReader));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}


