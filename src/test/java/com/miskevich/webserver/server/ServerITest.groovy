package com.miskevich.webserver.server

import com.miskevich.webserver.data.servlet.IndexServlet
import com.miskevich.webserver.server.util.ServletContext
import org.testng.annotations.Test

class ServerITest {

    @Test
    void testThreadPoolServerWithServlet() {
        IndexServlet indexServlet = new IndexServlet()

        ServletContext context = new ServletContext()
        context.addServlet("/index/all", indexServlet)

        Server server = new Server(3000)
        server.setResourcePath("src/test/resources/webapp")
        server.setServletContext(context)
        server.run()
    }

}
