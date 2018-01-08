package com.miskevich.webserver.server;

import com.miskevich.webserver.file.ResourceReader;
import com.miskevich.webserver.model.ServletRequest;
import com.miskevich.webserver.model.ServletResponse;
import com.miskevich.webserver.model.common.HttpStatus;
import com.miskevich.webserver.model.resources.Resource;
import com.miskevich.webserver.model.resources.StaticResourceRequest;
import com.miskevich.webserver.server.util.RequestParser;
import com.miskevich.webserver.server.util.ResponseHeaderGenerator;
import com.miskevich.webserver.server.util.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private BufferedReader reader;
    private InputStream inputStream;
    private BufferedOutputStream writer;
    private ResourceReader resourceReader;
    private ServletContext servletContext;

    public RequestHandler(BufferedReader reader, InputStream inputStream,
                          BufferedOutputStream writer, ResourceReader resourceReader, ServletContext servletContext) {
        this.reader = reader;
        this.inputStream = inputStream;
        this.writer = writer;
        this.resourceReader = resourceReader;
        this.servletContext = servletContext;
    }

    public void handle() {
        try (BufferedOutputStream writer = this.writer) {
            ServletRequest servletRequest = new ServletRequest();
            StaticResourceRequest staticResourceRequest = RequestParser.toRequest(reader, inputStream, servletRequest);
            servletRequest.setMethod(staticResourceRequest.getMethod());

            ServletResponse servletResponse = new ServletResponse();
            String fullUrlFromRequest = staticResourceRequest.getUrl();

            String urlForServlet = modifyURLIfParametersExist(fullUrlFromRequest, servletRequest);

            HttpServlet servlet = servletContext.getServlet(urlForServlet);

            if (servlet != null) {
                LOG.info("Found servlet: " + urlForServlet);
                processServlet(writer, servletRequest, servletResponse, servlet);
            } else {
                String url = staticResourceRequest.getUrl();
                if (url.matches("/.*/")) {
                    //TODO
                    url = "/movieland/index.html";
                    staticResourceRequest.setUrl(url);
                }
                LOG.info("URL for static resource: " + url);
                loadStaticResources(writer, staticResourceRequest);
            }
        } catch (IOException | ServletException e) {
            LOG.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    String modifyURLIfParametersExist(String fullUrlFromRequest, ServletRequest servletRequest) {
        if (fullUrlFromRequest.contains("?")) {
            int indexOfParameters = fullUrlFromRequest.indexOf("?");
            String urlWithoutParameters = fullUrlFromRequest.substring(0, indexOfParameters);
            LOG.info("UrlWithoutParameters: " + urlWithoutParameters);

            Map<String, String[]> requestParameters = getRequestParameters(fullUrlFromRequest, indexOfParameters);

            for (Map.Entry<String, String[]> parameter : requestParameters.entrySet()) {
                servletRequest.addParameter(parameter.getKey(), parameter.getValue());
            }
            return urlWithoutParameters;
        }
        return fullUrlFromRequest;
    }

    Map<String, String[]> getRequestParameters(String fullUrlFromRequest, int indexOfParameters) {
        Map<String, String[]> parameterMap = new HashMap<>();
        String parameters = fullUrlFromRequest.substring(indexOfParameters + 1);
        String[] splittedParameters = parameters.split("&");

        for (String parameter : splittedParameters) {
            String[] paramAndValue = parameter.split("=");
            parameterMap.put(paramAndValue[0], new String[]{paramAndValue[1]});
        }
        return parameterMap;
    }

    private void processServlet(BufferedOutputStream socketOutputStream, ServletRequest servletRequest,
                                ServletResponse servletResponse, HttpServlet servlet) throws ServletException, IOException {

        //processing
        servlet.service(servletRequest, servletResponse);

        if (HttpStatus.OK.getStatusCode() == servletResponse.getStatus()) {
            //response header
            String responseHeaders = ResponseHeaderGenerator.generateHeadersForServlet(servletResponse, false);
            socketOutputStream.write(responseHeaders.getBytes());

            //response body
            byte[] bytes = servletResponse.getContent().toByteArray();
            socketOutputStream.write(bytes);
        } else {
            socketOutputStream.write(ResponseHeaderGenerator.generateHeadersForServlet(servletResponse, true).getBytes());
        }
    }

    private void loadStaticResources(BufferedOutputStream writer, StaticResourceRequest staticResourceRequest) {
        Resource resource = resourceReader.getResource(staticResourceRequest.getUrl());
        try (BufferedInputStream content = resource.getContent()) {

            if (content.available() != 0) {
                //response header
                String headersForStaticResources = ResponseHeaderGenerator.generateHeadersForStaticResources(resource, false);
                writer.write(headersForStaticResources.getBytes());

                //response body
                //buffer size is 128 Kb, based on performance test results:
                // CPU on Windows is reached to 100% with 256 Kb buffer
                byte[] buffer = new byte[1024 * 128];
                int length;
                while ((length = content.read(buffer)) != -1) {
                    writer.write(buffer, 0, length);
                }
            } else {
                writer.write(ResponseHeaderGenerator.generateHeadersForStaticResources(resource, true).getBytes());
            }
        } catch (IOException e) {
            LOG.error("ERROR: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        handle();
    }
}
