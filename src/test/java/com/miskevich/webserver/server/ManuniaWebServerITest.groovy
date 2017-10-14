package com.miskevich.webserver.server

class ManuniaWebServerITest {

    static void main(String[] args) {
        ManuniaWebServer manuniaWebServer = new ManuniaWebServer('src/test/resources/itest', 3000)
        manuniaWebServer.start()
    }
}
