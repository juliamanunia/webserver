package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;
import com.miskevich.webserver.model.Request;
import com.miskevich.webserver.server.util.Consumer;
import com.miskevich.webserver.server.util.Producer;
import com.miskevich.webserver.server.util.RequestParser;
import com.miskevich.webserver.server.util.ResponseHeaderGenerator;

import java.io.*;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class RequestHandler {

    private BufferedReader reader;
    private BufferedOutputStream writer;
    private ResourceReader resourceReader;

    public RequestHandler(BufferedReader reader, BufferedOutputStream writer, ResourceReader resourceReader) {
        this.reader = reader;
        this.writer = writer;
        this.resourceReader = resourceReader;
    }

    public void handle() {
        try (BufferedOutputStream writer = this.writer){
            Request request = RequestParser.toRequest(reader);
            BufferedInputStream resource = resourceReader.getResource(request.getUrl());

            if(resource.available() != 0){
                writer.write(ResponseHeaderGenerator.generate(false).getBytes());
//                byte[] buffer = new byte[1024 * 8];
//                int length;
//                while ((length = resource.read(buffer)) != -1){
//                    writer.write(buffer, 0, length);
//                }

                Queue<Map<byte[], Integer>> queue = new ArrayBlockingQueue<>(1000);
                Producer producer = new Producer(queue, resource);
                Consumer consumer1 = new Consumer(queue, writer);
//                Consumer consumer2 = new Consumer(queue, writer);
//                Consumer consumer3 = new Consumer(queue, writer);

                Thread pthread = new Thread(producer);
                pthread.start();
                Thread c1thread = new Thread(consumer1);
                c1thread.start();
//                Thread c2thread = new Thread(consumer2);
//                c2thread.start();
//                Thread c3thread = new Thread(consumer3);
//                c3thread.start();
                pthread.join();
                c1thread.join();
//                c2thread.join();
//                c3thread.join();

            }else {
                writer.write(ResponseHeaderGenerator.generate(true).getBytes());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

//    public void handle() {
//        try (BufferedOutputStream writer = this.writer){
//            Request request = RequestParser.toRequest(reader);
//            BufferedInputStream resource = resourceReader.getResource(request.getUrl());
//
//            if(resource.available() != 0){
//                writer.write(ResponseHeaderGenerator.generate(false).getBytes());
//                byte[] buffer = new byte[1024 * 8];
//                int length;
//                while ((length = resource.read(buffer)) != -1){
//                    writer.write(buffer, 0, length);
//                }
//            }else {
//                writer.write(ResponseHeaderGenerator.generate(true).getBytes());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
