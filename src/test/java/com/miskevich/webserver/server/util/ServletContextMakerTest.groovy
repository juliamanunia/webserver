package com.miskevich.webserver.server.util

import com.miskevich.webserver.server.util.ServletContextMaker
import com.miskevich.webserver.server.util.reader.XMLServletReader
import org.testng.annotations.Test

import javax.servlet.http.HttpServlet
import java.nio.file.Paths

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

class ServletContextMakerTest {

    @Test
    void testUnzipAndFindWebXml() {
        ServletContextMaker contextMaker = new ServletContextMaker('tomcat-example-0.0.1.war')
        contextMaker.unzip()
        assertEquals(contextMaker.findWebXml(), 'src/main/webapp/tomcat-example-0.0.1/WEB-INF/web.xml')

        ServletContextMaker contextMaker1 = new ServletContextMaker('example.war')
        contextMaker1.unzip()
        assertEquals(contextMaker1.findWebXml(), 'web.xml doesn\'t exist in src/main/webapp/example/WEB-INF!!!')

        ServletContextMaker contextMaker2 = new ServletContextMaker('example-0.0.1.war')
        contextMaker2.unzip()
        assertEquals(contextMaker2.findWebXml(), 'src/main/webapp/example-0.0.1/WEB-INF/web.xml')
    }

    @Test
    void testFindWebXmlSuccess(){
        def expectedWebXml = Paths.get('src', 'test', 'resources', 'tomcat-example-0.0.1/WEB-INF', 'web.xml').toString()

        ServletContextMaker contextMaker = new ServletContextMaker('tomcat-example-0.0.1.war')
        def actualWebXml = contextMaker.findWebXml(new File('src/test/resources/tomcat-example-0.0.1/WEB-INF'))
        assertEquals(actualWebXml, expectedWebXml)
    }

    @Test(expectedExceptions = NoSuchFieldException.class, expectedExceptionsMessageRegExp = 'web.xml doesn\'t exist in src/test/resources/example!!!')
    void testFindWebXmlFailure(){
        ServletContextMaker contextMaker = new ServletContextMaker('example')
        contextMaker.findWebXml(new File('src/test/resources/example'))
    }

    @Test
    void testInitializeServlets(){
        XMLServletReader xmlServletReader = new XMLServletReader()
        def servletDefinitions = xmlServletReader.getServlets('src/test/resources/tomcat-example-0.0.1/WEB-INF/web.xml')
        ServletContextMaker contextMaker = new ServletContextMaker('src/test/resources/tomcat-example-0.0.1/WEB-INF')
        def context = contextMaker.initializeServlets(servletDefinitions, new File('src/test/resources/tomcat-example-0.0.1/WEB-INF'))
        def servletHolder = context.getServletHolder()

        for (Map.Entry<String, HttpServlet> servlet : servletHolder.entrySet()){
            assertEquals(servlet.getKey(),'/example')
            assertTrue(servlet.getValue().getClass().getSuperclass() == HttpServlet.class)
        }
    }
}