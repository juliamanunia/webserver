package com.miskevich.webserver.server


class ServerITest extends GroovyTestCase{

    void testServerOnRealProject(){
        Server server = new Server(3000)
        server.setResourcePath("src/test/resources/webapp")
        server.start()
    }

    void testThreadPoolServerOnRealProject(){
        ThreadPooledServer threadPooledServer = new ThreadPooledServer(3000)
        threadPooledServer.setResourcePath("src/test/resources/webapp")
        threadPooledServer.run()
    }

}
