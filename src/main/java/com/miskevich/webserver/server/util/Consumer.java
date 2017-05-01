package com.miskevich.webserver.server.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;

public class Consumer implements Runnable {

    private Queue<Map<byte[], Integer>> queue;
    private BufferedOutputStream writer;

    public Consumer(Queue<Map<byte[], Integer>> queue, BufferedOutputStream writer) {
        this.queue = queue;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            while (queue.size() != 0){
                byte[] buffer = new byte[1024 * 8];
                int length = 0;
                Map<byte[], Integer> byteToLength = queue.poll();

                for (Map.Entry<byte[], Integer> entry : byteToLength.entrySet()){
                    buffer = entry.getKey();
                    length = entry.getValue();
                }
                writer.write(buffer, 0, length);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
