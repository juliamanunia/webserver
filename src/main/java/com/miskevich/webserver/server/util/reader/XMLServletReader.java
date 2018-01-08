package com.miskevich.webserver.server.util.reader;


import com.miskevich.webserver.model.ServletDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.miskevich.webserver.model.common.WebXmlTags.*;

public class XMLServletReader implements ServletReader {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public List<ServletDefinition> getServlets(File webXmlPath) {
        List<ServletDefinition> servletDefinitions = new ArrayList<>();

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(webXmlPath);
            document.getDocumentElement().normalize();
            NodeList servlet = document.getElementsByTagName(SERVLET.getTagName());

            for (int i = 0; i < servlet.getLength(); i++) {
                Node servletNode = servlet.item(i);
                servletDefinitions.add(getServletDefinition(servletNode, document));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return servletDefinitions;
    }

    private ServletDefinition getServletDefinition(Node servletNode, Document document) {
        String servletName = "";
        ServletDefinition servletDefinition = new ServletDefinition();
        if (servletNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) servletNode;
            servletName = element.getElementsByTagName(SERVLET_NAME.getTagName()).item(0).getTextContent();
            String servletClass = element.getElementsByTagName(SERVLET_CLASS.getTagName()).item(0).getTextContent();
            servletDefinition.setName(servletName);
            servletDefinition.setClassName(servletClass);
        }

        NodeList servletMapping = document.getElementsByTagName(SERVLET_MAPPING.getTagName());
        List<String> urls = new ArrayList<>();
        for (int j = 0; j < servletMapping.getLength(); j++) {
            Node servletMappingNode = servletMapping.item(j);
            if (servletMappingNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) servletMappingNode;
                String mappingName = element.getElementsByTagName(SERVLET_NAME.getTagName()).item(0).getTextContent();
                if (servletName.equals(mappingName)) {
                    String servletUrl = element.getElementsByTagName(URL_PATTERN.getTagName()).item(0).getTextContent();
                    urls.add(servletUrl);
                }
            }
        }
        servletDefinition.setUrls(urls);
        return servletDefinition;
    }
}
