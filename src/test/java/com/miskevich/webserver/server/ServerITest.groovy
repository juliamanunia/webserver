package com.miskevich.webserver.server

import org.testng.annotations.Test


class ServerITest extends GroovyTestCase{

    @Test
    void testServerOnRealProject(){
        Server server = new Server(3000)
        server.setResoursePath("src/test/resources/webapp")
        server.start()
    }

}
