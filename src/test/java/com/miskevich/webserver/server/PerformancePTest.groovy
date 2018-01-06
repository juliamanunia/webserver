package com.miskevich.webserver.server

import org.testng.annotations.Test

class PerformancePTest {

    @Test
    void testPerformanceForResourceLoading() {

        final int ATTEMPTS = 20
        def total = 0

        for (int i = 1; i < ATTEMPTS; i++) {
            def start = System.currentTimeMillis()

            def socket = new Socket("localhost", 3000)
            def writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
            writer.write("GET /pic/" + i + ".jpg HTTP/1.1\n\n")
            writer.flush()

            def inputStream = socket.getInputStream()
            def reader = new BufferedReader(new InputStreamReader(inputStream))

            long contentLength = 0
            String value
            while (!(value = reader.readLine()).isEmpty()) {
                if (value.contains("Content-Length:")) {
                    contentLength = Long.parseLong(value.substring(value.indexOf(" ") + 1))
                }

            }

            for (int j = 0; j < contentLength; j++) {
                inputStream.read()
            }

            def end = System.currentTimeMillis() - start
            total += end
            println "Request took " + end + " ms"
            socket.close()
        }

        println "Average: " + (total / ATTEMPTS)

    }

}
