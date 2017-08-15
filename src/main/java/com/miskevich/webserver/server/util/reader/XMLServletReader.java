package com.miskevich.webserver.server.util.reader;


import com.miskevich.webserver.model.ServletDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLServletReader implements ServletReader {

    public List<ServletDefinition> getServlets(String path) {
        List<ServletDefinition> servletDefinitions = new ArrayList<>();

        try {
            File inputFile = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputFile);
            document.getDocumentElement().normalize();
            NodeList servlet = document.getElementsByTagName("servlet");

            for (int i = 0; i < servlet.getLength(); i++) {
                Node servletNode = servlet.item(i);
                servletDefinitions.add(getServletDefinition(servletNode, document));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        return servletDefinitions;
    }

    private ServletDefinition getServletDefinition(Node servletNode, Document document) {
        String servletName = "";
        ServletDefinition servletDefinition = new ServletDefinition();
        if (servletNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) servletNode;
            servletName = element.getElementsByTagName("servlet-name").item(0).getTextContent();
            String servletClass = element.getElementsByTagName("servlet-class").item(0).getTextContent();
            servletDefinition.setName(servletName);
            servletDefinition.setClassName(servletClass);
        }

        NodeList servletMapping = document.getElementsByTagName("servlet-mapping");
        List<String> urls = new ArrayList<>();
        for (int j = 0; j < servletMapping.getLength(); j++) {
            Node servletMappingNode = servletMapping.item(j);
            if (servletMappingNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) servletMappingNode;
                String mappingName = element.getElementsByTagName("servlet-name").item(0).getTextContent();
                if (servletName.equals(mappingName)) {
                    String servletUrl = element.getElementsByTagName("url-pattern").item(0).getTextContent();
                    urls.add(servletUrl);
                }
            }
        }
        servletDefinition.setUrls(urls);
        return servletDefinition;
    }
}
