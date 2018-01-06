package com.miskevich.webserver.server

import com.miskevich.webserver.file.ResourceReader
import com.miskevich.webserver.model.ServletRequest
import com.miskevich.webserver.server.util.ServletContext
import org.mockito.Mockito
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import static org.testng.Assert.assertEquals

class RequestHandlerTest {
    RequestHandler requestHandler

    @BeforeTest
    void initializeRequestHandler(){
        requestHandler = new RequestHandler(Mockito.mock(BufferedReader.class), Mockito.mock(InputStream.class),
                Mockito.mock(BufferedOutputStream.class), Mockito.mock(ResourceReader.class), new ServletContext())
    }

    @Test
    void testDetermineURLForServletCheckParametersExist() {
        def expectedURL = '/review'
        def actualURL = requestHandler.modifyURLIfParametersExist('/review?review=27&data=hello', Mockito.mock(ServletRequest.class))
        assertEquals(actualURL, expectedURL)
    }

    @Test
    void testDetermineURLForServletCheckNoParameters() {
        def expectedURL = '/review'
        def actualURL = requestHandler.modifyURLIfParametersExist('/review', Mockito.mock(ServletRequest.class))
        assertEquals(actualURL, expectedURL)
    }

    @Test
    void  testGetRequestParameters(){
        def expectedParameters = [review:['27'].toArray(), data:['hello'].toArray()]
        def actualParameters = requestHandler.getRequestParameters('/review?review=27&data=hello', 7)
        assertEquals(actualParameters, expectedParameters)
    }
}
