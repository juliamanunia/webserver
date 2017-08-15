package com.miskevich.webserver.server.util.zip

import org.testng.annotations.Test

class UnzipperTest {

    @Test
    void testUnzipAndFindWebXml() {
        Unzipper unzipper = new Unzipper('tomcat-example-0.0.1.war')
        unzipper.unzip()
        assertEquals(unzipper.findWebXml(), 'src/main/webapp/tomcat-example-0.0.1/WEB-INF/web.xml')

        Unzipper unzipper2 = new Unzipper('example.war')
        unzipper2.unzip()
        assertEquals(unzipper2.findWebXml(), 'web.xml doesn\'t exist in src/main/webapp/example/WEB-INF!!!')

        Unzipper unzipper3 = new Unzipper('example-0.0.1.war')
        unzipper3.unzip()
        assertEquals(unzipper3.findWebXml(), 'src/main/webapp/example-0.0.1/WEB-INF/web.xml')
    }
}
