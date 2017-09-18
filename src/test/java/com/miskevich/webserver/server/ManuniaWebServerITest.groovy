package com.miskevich.webserver.server

import com.miskevich.webserver.server.util.DirectoryScanner
import com.miskevich.webserver.server.util.ServletContext
import org.testng.annotations.Test

class ManuniaWebServerITest {

    @Test
    void runServer() {
        def context = new ServletContext()
        DirectoryScanner directoryScanner = new DirectoryScanner(context)
        while (true) {
            directoryScanner.scanDirectoryForNewWars()
        }
    }
}
