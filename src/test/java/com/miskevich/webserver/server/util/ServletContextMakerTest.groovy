package com.miskevich.webserver.server.util

import com.miskevich.webserver.exception.WebXmlFileException
import com.miskevich.webserver.server.util.reader.XMLServletReader
import org.mockito.Mockito
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import javax.servlet.http.HttpServlet

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

class ServletContextMakerTest {

    private ServletContextMaker servletContextMaker

    @BeforeTest
    void initialize() {
        servletContextMaker = new ServletContextMaker(Mockito.mock(ServletContext.class))
    }

    @Test
    void testGenerateDirectoryNameForUnzippedFile() {
        def actualDirNameForUnzippedFile = servletContextMaker.generateDirectoryNameForUnzippedFile('tomcat-example-0.0.1.war')
        println actualDirNameForUnzippedFile
        assertEquals(actualDirNameForUnzippedFile, new File('tomcat-example-0.0.1'))
    }

    @Test
    void testGenerateDirectoryNameForWebXml() {
        File actualDirForWebXml = servletContextMaker.generateDirectoryNameForWebXml(new File('tomcat-example-0.0.1'))
        def path = actualDirForWebXml.path
        def actualRelativePath = path.substring(path.indexOf('tomcat-example-0.0.1'))
        assertEquals(actualRelativePath, 'tomcat-example-0.0.1/WEB-INF')
    }

    @Test
    void testUnzip() {
        ServletContextMaker servletContextMaker = new ServletContextMaker('src/test/resources/tomcat-example-0.0.1.war', Mockito.mock(ServletContext.class))
        servletContextMaker.unzip(new File('src/test/resources/tomcat-example-0.0.1'))
    }

    @Test
    void testFindWebXml() {
        assertTrue(servletContextMaker.findWebXml(new File('src/test/resources/tomcat-example-0.0.1/WEB-INF')))
    }

    @Test(expectedExceptions = WebXmlFileException.class,
            expectedExceptionsMessageRegExp = 'web.xml doesn\'t exist in src/test/resources/example/WEB-INF')
    void testFindWebXmlNoFileExists() {
        servletContextMaker.findWebXml(new File('src/test/resources/example/WEB-INF'))
    }

    @Test
    void testInitializeServlets() {
        XMLServletReader xmlServletReader = new XMLServletReader()
        def servletDefinitions = xmlServletReader.getServlets(new File('src/test/resources/tomcat-example-0.0.1/WEB-INF/web.xml'))
        ServletContextMaker contextMaker = new ServletContextMaker('src/test/resources/tomcat-example-0.0.1/WEB-INF', Mockito.mock(ServletContext.class))
        contextMaker.generateDirectoryNameForWebXml(new File('tomcat-example-0.0.1.war'))

        def context = contextMaker.initializeServlets(servletDefinitions, new File('src/test/resources/tomcat-example-0.0.1/WEB-INF'))

        def servletHolder = context.getServletHolder()

        for (Map.Entry<String, HttpServlet> servlet : servletHolder.entrySet()) {
            assertEquals(servlet.getKey(), 'tomcat-example-0.0.1/example')
            assertTrue(servlet.getValue().getClass().getSuperclass() == HttpServlet.class)
        }
    }
}
