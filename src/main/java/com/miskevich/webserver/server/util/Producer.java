package com.miskevich.webserver.server.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Producer implements Runnable {

    private Map<byte[], Integer> byteToLength = new HashMap<>();
    private Queue<Map<byte[], Integer>> queue;
    private BufferedInputStream resource;

    public Producer(Queue<Map<byte[], Integer>> queue, BufferedInputStream resource) {
        this.queue = queue;
        this.resource = resource;
    }

    @Override
    public void run() {
//int i = 0;
        int length;
        byte[] buffer = new byte[1024 * 8];
        try {
            while ((length = resource.read(buffer)) != -1){
                byteToLength.put(buffer, length);
                queue.add(byteToLength);
//                i++;
//                System.out.println("read buffer: " + i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
