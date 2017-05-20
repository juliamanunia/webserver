package com.miskevich.webserver.server.util

import org.testng.annotations.Test

class ServletResponseHeaderGeneratorTest extends GroovyTestCase {

    @Test(dataProvider = "provideResponseNotNull", dataProviderClass = ResponseDataProvider.class)
    void testGenerateHeadersForServletNotNull(response, expected) {
        def servletHeaders = ResponseHeaderGenerator.generateHeadersForServlet(response, false)
        def servletHeadersWithoutDate = servletHeaders.substring(0, servletHeaders.indexOf('Date'))
        servletHeadersWithoutDate += servletHeaders.substring(servletHeaders.indexOf('GMT\n') + 4)
        assertEquals(servletHeadersWithoutDate, expected.toString())
    }

    @Test(dataProvider = "provideResponseNull", dataProviderClass = ResponseDataProvider.class)
    void testGenerateHeadersForServletNull(response, expected) {
        def servletHeaders = ResponseHeaderGenerator.generateHeadersForServlet(response, false)
        def servletHeadersWithoutDate = servletHeaders.substring(0, servletHeaders.indexOf('Date'))
        servletHeadersWithoutDate += servletHeaders.substring(servletHeaders.indexOf('GMT\n') + 4)
        assertEquals(servletHeadersWithoutDate, expected.toString())
    }

    @Test(dataProvider = "provideResponseStaticResource", dataProviderClass = ResponseDataProvider.class)
        void testGenerateHeadersForStaticResources(resource, expected) {
        def staticResourceHeaders = ResponseHeaderGenerator.generateHeadersForStaticResources(resource, true)
        def staticResourceHeadersWithoutDate = staticResourceHeaders.substring(0, staticResourceHeaders.indexOf('Date'))
        staticResourceHeadersWithoutDate += staticResourceHeaders.substring(staticResourceHeaders.indexOf('GMT\n') + 4)
        assertEquals(staticResourceHeadersWithoutDate, expected.toString())
    }

}
