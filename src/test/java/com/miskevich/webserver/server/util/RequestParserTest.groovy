package com.miskevich.webserver.server.util

import com.miskevich.webserver.model.Request
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import static org.testng.Assert.*


class RequestParserTest extends GroovyTestCase {

    private BufferedReader reader

    @DataProvider (name = "provideRequest")
    Object[][] provideRequest() {
        def headersUrlMethod = new StringBuilder()
        headersUrlMethod.append("GET /index.html HTTP/1.1\n")
                .append("Host: localhost\n")
                .append("Connection: keep-alive\n")
                .append("Cache-Control: max-age=0\n")
                .append("Upgrade-Insecure-Requests: 1\n")
                .append("User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36\n")
                .append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n")
                .append("Accept-Encoding: gzip, deflate, sdch, br\n")
                .append("Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4\n")
                .append("Cookie: JSESSIONID=dskfw774f9ovjz1isafe83cr; io=oV8FgNcgYFEpWDc5AAB4\n")
                .append("\n")

        def headersMap = new HashMap()
        headersMap.put("Host", " localhost")
        headersMap.put("Connection", " keep-alive")
        headersMap.put("Cache-Control", " max-age=0")
        headersMap.put("Upgrade-Insecure-Requests", " 1")
        headersMap.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
        headersMap.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
        headersMap.put("Accept-Encoding", " gzip, deflate, sdch, br")
        headersMap.put("Accept-Language", " ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
        headersMap.put("Cookie", " JSESSIONID=dskfw774f9ovjz1isafe83cr; io=oV8FgNcgYFEpWDc5AAB4")

        def expectedRequest = new Request(url: '/index.html', method: 'GET', headers: headersMap)

        def array = new Object[1][]
        array[0] = [headersUrlMethod, expectedRequest]
        return array
    }

    @Test(dataProvider = "provideRequest")
    void testToRequest(requestSB, expected) {
        reader = new BufferedReader(new StringReader(requestSB.toString()))
        def request = RequestParser.toRequest(reader)
        assertEquals(request, expected)
    }
}
