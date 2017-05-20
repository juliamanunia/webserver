package com.miskevich.webserver.server.util

import com.miskevich.webserver.model.resources.Resource
import com.miskevich.webserver.model.ServletResponse
import org.testng.annotations.DataProvider


class ResponseDataProvider {

    @DataProvider (name = "provideResponseNotNull")
    static Object[][] provideResponseNotNull() {

        def response = new ServletResponse()
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
                .append("Server: ")
                .append("Miskevich/0.0.1")
                .append("\n")
                .append("Cache-Control: ")
                .append("max-age=3600")
                .append("\n")
                .append("\n")

        def array = new Object[1][]
        array[0] = [response, expectedResponse]
        return array
    }

    @DataProvider (name = "provideResponseNull")
    static Object[][] provideResponseNull() {

        def response = new ServletResponse()

        def expectedResponse = new StringBuilder()
        expectedResponse
                .append("HTTP/1.1 200 OK")
                .append("\n")
                .append("Server: ")
                .append("Miskevich/0.0.1")
                .append("\n")
                .append("Cache-Control: ")
                .append("max-age=3600")
                .append("\n")
                .append("\n")

        def array = new Object[1][]
        array[0] = [response, expectedResponse]
        return array
    }

    @DataProvider (name = "provideResponseStaticResource")
    static Object[][] provideResponseStaticResource() {

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
                .append("Server: ")
                .append("Miskevich/0.0.1")
                .append("\n")
                .append("Cache-Control: ")
                .append("max-age=3600")
                .append("\n")
                .append("\n")

        def array = new Object[1][]
        array[0] = [resource, expectedResponse]
        return array
    }
}
