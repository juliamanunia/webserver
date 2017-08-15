package com.miskevich.webserver.providers

import com.miskevich.webserver.model.ServletDefinition
import org.testng.annotations.DataProvider

class WebXmlDefinitionsProvider {

    @DataProvider(name = 'provideWevXmlDefinitions')
    static Object[][] provideWevXmlDefinitions(){
        def servletDefinitions = [
                new ServletDefinition(name: 'ExampleServlet', className: 'com.miskevich.tomcatexample.web.servlet.ExampleServlet', urls: ['/example']),
                new ServletDefinition(name: 'SecondServlet', className: 'com.miskevich.tomcatexample.web.servlet.SecondServlet', urls: ['/second', '/secondNext'])
        ]

        def array = new Object[1][]
        array[0] = [servletDefinitions]
        return array
    }
}
