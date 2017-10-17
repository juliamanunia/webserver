package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;
import com.miskevich.webserver.server.util.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Server implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private int port;
    private ResourceReader resourceReader;
    private ServletContext servletContext;
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 20,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
                RequestHandler requestHandler = new RequestHandler(reader, inputStream, writer, resourceReader, servletContext);
                poolExecutor.execute(requestHandler);
            }
        } catch (IOException e) {
            LOG.error("Error: ", e);
        }
    }

    public void setResourcePath(String path) {
        resourceReader = new ResourceReader(path);
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}


