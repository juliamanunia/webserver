package com.miskevich.webserver.server;

import java.io.*;

public class FileToBufferTest {
    public static void main(String[] args) throws IOException {

        final int ATTEMPTS = 21;
        int total = 0;

        for (int i = 1; i < ATTEMPTS; i++) {
            long start = System.currentTimeMillis();
            InputStream inputStream = new FileInputStream(
                    new File("webserver/src/test/resources/webapp/pic/" + i + ".jpg"));

            byte[] buffer = new byte[1024 * 32];
            int length;
            while ((length = inputStream.read(buffer)) != -1){
                System.out.println(length);
            }

            long end = System.currentTimeMillis() - start;
            total += end;
            System.out.println( "Request took " + end + " ms");
        }

        System.out.println("Average: " + (total / ATTEMPTS));
    }
}
