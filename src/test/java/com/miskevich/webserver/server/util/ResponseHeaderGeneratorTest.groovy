package com.miskevich.webserver.server.util

import com.miskevich.webserver.model.Resource
import com.miskevich.webserver.model.Response
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class ResponseHeaderGeneratorTest extends GroovyTestCase {

    @Test(dataProvider = "provideResponseNotNull")
    void testGenerateHeadersForServletNotNull(response, expected) {
        def servletHeaders = ResponseHeaderGenerator.generateHeadersForServlet(response, false)
        def servletHeadersWithoutDate = servletHeaders.substring(0, servletHeaders.indexOf('Date'))
        servletHeadersWithoutDate += servletHeaders.substring(servletHeaders.indexOf('GMT\n') + 4)
        assertEquals(servletHeadersWithoutDate, expected.toString())
    }

    @Test(dataProvider = "provideResponseNull")
    void testGenerateHeadersForServletNull(response, expected) {
        def servletHeaders = ResponseHeaderGenerator.generateHeadersForServlet(response, false)
        def servletHeadersWithoutDate = servletHeaders.substring(0, servletHeaders.indexOf('Date'))
        servletHeadersWithoutDate += servletHeaders.substring(servletHeaders.indexOf('GMT\n') + 4)
        assertEquals(servletHeadersWithoutDate, expected.toString())
    }

    @Test(dataProvider = "provideResponseStaticResource")
        void testGenerateHeadersForStaticResources(resource, expected) {
        def staticResourceHeaders = ResponseHeaderGenerator.generateHeadersForStaticResources(resource, true)
        def staticResourceHeadersWithoutDate = staticResourceHeaders.substring(0, staticResourceHeaders.indexOf('Date'))
        staticResourceHeadersWithoutDate += staticResourceHeaders.substring(staticResourceHeaders.indexOf('GMT\n') + 4)
        assertEquals(staticResourceHeadersWithoutDate, expected.toString())
    }


    @DataProvider (name = "provideResponseNotNull")
    Object[][] provideResponseNotNull() {

        def response = new Response(new ByteArrayOutputStream())
        response.setContentType('image/jpeg')
        response.setContentLengthLong(55)


        def expectedResponse = new StringBuilder()
        expectedResponse
                .append("HTTP/1.1 200 OK")
                .append("\n")
                .append("Content-Type: image/jpeg")
                .append("\n")
                .append("Content-Length: 55")
                .append("\n")
                .append("\n")

        def array = new Object[1][]
        array[0] = [response, expectedResponse]
        return array
    }

    @DataProvider (name = "provideResponseNull")
    Object[][] provideResponseNull() {

        def response = new Response(new ByteArrayOutputStream())

        def expectedResponse = new StringBuilder()
        expectedResponse
                .append("HTTP/1.1 200 OK")
                .append("\n")
                .append("\n")

        def array = new Object[1][]
        array[0] = [response, expectedResponse]
        return array
    }

    @DataProvider (name = "provideResponseStaticResource")
    Object[][] provideResponseStaticResource() {

        def resource = new Resource()
        resource.setContentType('image/jpeg')
        resource.setContentLength(44)
        resource.setResourceLocalPath(new File('test'))

        def expectedResponse = new StringBuilder()
        expectedResponse
                .append("HTTP/1.1 404 Not found")
                .append("\n")
                .append("Content-Type: image/jpeg")
                .append("\n")
                .append("Content-Length: 44")
                .append("\n")
                .append("\n")

        def array = new Object[1][]
        array[0] = [resource, expectedResponse]
        return array
    }
}
