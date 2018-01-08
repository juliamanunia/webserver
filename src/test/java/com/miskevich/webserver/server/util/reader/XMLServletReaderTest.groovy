package com.miskevich.webserver.server.util.reader

import com.miskevich.webserver.providers.WebXmlDefinitionsProvider
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

class XMLServletReaderTest {

    @Test(dataProvider = 'provideWevXmlDefinitions', dataProviderClass = WebXmlDefinitionsProvider.class)
    void testGetServlets(def expectedServletDefinitions) {
        XMLServletReader xmlServletReader = new XMLServletReader()
        def actualServletDefinitions = xmlServletReader.getServlets(new File('src/test/resources/web.xml'))
        assertEquals(actualServletDefinitions, expectedServletDefinitions)
    }
}
