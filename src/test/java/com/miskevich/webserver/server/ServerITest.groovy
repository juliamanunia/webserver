package com.miskevich.webserver.server

import org.testng.annotations.Test


class ServerITest extends GroovyTestCase{

    @Test
    void testServerOnRealProject(){
        Server server = new Server(3000)
        server.setResoursePath("src/test/resources/webapp")
        server.start()
    }

    @Test
    void testThreadPoolServerOnRealProject(){
        ThreadPooledServer threadPooledServer = new ThreadPooledServer(3000)
        threadPooledServer.setResourcePath("src/test/resources/webapp")
        new Thread(threadPooledServer).start()

        try {
            Thread.sleep(1000000)
        } catch (InterruptedException e) {
            throw new RuntimeException(e)
        }
    }

}
