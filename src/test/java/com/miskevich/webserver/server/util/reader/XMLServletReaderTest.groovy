package com.miskevich.webserver.server.util.reader

import org.testng.annotations.Test
import com.miskevich.webserver.providers.WebXmlDefinitionsProvider
import static org.testng.Assert.assertEquals

class XMLServletReaderTest {

    @Test(dataProvider = 'provideWevXmlDefinitions', dataProviderClass = WebXmlDefinitionsProvider.class)
    void testGetServlets(def expectedServletDefinitions) {
        XMLServletReader xmlServletReader = new XMLServletReader()
        def actualServletDefinitions = xmlServletReader.getServlets('src/test/resources/web.xml')
        assertEquals(actualServletDefinitions, expectedServletDefinitions)
    }
}
