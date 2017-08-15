package com.miskevich.webserver.server

import com.miskevich.webserver.server.util.ServletContext
import com.miskevich.webserver.data.servlet.*
import org.testng.annotations.Test

class ServerITest {

    @Test
    void testThreadPoolServerWithServlet() {
        IndexServlet indexServlet = new IndexServlet()

        ServletContext context = new ServletContext()
        context.addServlet("/index/all", indexServlet)

        Server threadPooledServer = new Server(3000)
        threadPooledServer.setResourcePath("src/test/resources/webapp")
        threadPooledServer.setServletContext(context)
        threadPooledServer.run()
    }

}
