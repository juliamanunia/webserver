package com.miskevich.webserver.server

import com.miskevich.webserver.server.util.DirectoryScanner
import org.testng.annotations.Test

class ManuniaWebServerITest {

    @Test
    void runServer(){
        DirectoryScanner directoryScanner = new DirectoryScanner()
        while (true) {
            directoryScanner.scanDirectoryForNewWars()
        }
    }
}
