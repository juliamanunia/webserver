package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class WorkerRunnable implements Runnable {

    private Socket socket;
    private ResourceReader resourceReader;

    public WorkerRunnable(Socket socket, ResourceReader resourceReader) {
        this.socket = socket;
        this.resourceReader = resourceReader;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
            RequestHandler requestHandler = new RequestHandler(reader, writer, resourceReader);
            requestHandler.handle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
