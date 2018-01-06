package com.miskevich.webserver.server.util

import com.miskevich.webserver.server.util.reader.XMLServletReader
import org.testng.annotations.Test

import javax.servlet.http.HttpServlet
import java.nio.file.Paths

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

class ServletContextMakerTest {
    //Comment which should be removed

    @Test
    void testUnzipAndFindWebXml() {
        def context = new ServletContext()
        ServletContextMaker contextMaker = new ServletContextMaker('src/test/resources/tomcat-example-0.0.1.war', context)
        contextMaker.generateDirectoryNames('tomcat-example-0.0.1.war')
        contextMaker.unzip()
        //assertEquals(contextMaker.findWebXml(), 'src/main/webapp/tomcat-example-0.0.1/WEB-INF/web.xml')

//        ServletContextMaker contextMaker1 = new ServletContextMaker('example.war', context)
//        contextMaker1.unzip()
//        assertEquals(contextMaker1.findWebXml(), 'web.xml doesn\'t exist in src/main/webapp/example/WEB-INF!!!')
//
//        ServletContextMaker contextMaker2 = new ServletContextMaker('example-0.0.1.war', context)
//        contextMaker2.unzip()
//        assertEquals(contextMaker2.findWebXml(), 'src/main/webapp/example-0.0.1/WEB-INF/web.xml')
    }

    @Test
    void testFindWebXmlSuccess() {
        def context = new ServletContext()
        def expectedWebXml = Paths.get('src', 'test', 'resources', 'tomcat-example-0.0.1/WEB-INF', 'web.xml').toString()

        ServletContextMaker contextMaker = new ServletContextMaker('tomcat-example-0.0.1.war', context)
        def actualWebXml = contextMaker.findWebXml(new File('src/test/resources/tomcat-example-0.0.1/WEB-INF'))
        assertEquals(actualWebXml, expectedWebXml)
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = 'web.xml doesn\'t exist in src/test/resources/example!!!')
    void testFindWebXmlFailure() {
        def context = new ServletContext()
        ServletContextMaker contextMaker = new ServletContextMaker('example', context)
        contextMaker.findWebXml(new File('src/test/resources/example'))
    }

    @Test
    void testInitializeServlets() {
        def dummyContext = new ServletContext()
        XMLServletReader xmlServletReader = new XMLServletReader()
        def servletDefinitions = xmlServletReader.getServlets('src/test/resources/tomcat-example-0.0.1/WEB-INF/web.xml')
        ServletContextMaker contextMaker = new ServletContextMaker('src/test/resources/tomcat-example-0.0.1/WEB-INF', dummyContext)
        def context = contextMaker.initializeServlets(servletDefinitions, new File('src/test/resources/tomcat-example-0.0.1/WEB-INF'))
        def servletHolder = context.getServletHolder()

        for (Map.Entry<String, HttpServlet> servlet : servletHolder.entrySet()) {
            assertEquals(servlet.getKey(), 'null/example')
            assertTrue(servlet.getValue().getClass().getSuperclass() == HttpServlet.class)
        }
    }
}
