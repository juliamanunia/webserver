package com.miskevich.webserver.server.servlet;

import com.miskevich.webserver.server.templater.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IndexServlet extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {

        //request parse
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("message", "");

        //response generateHeadersForStaticResources
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        BufferedWriter bufferedWriter = new BufferedWriter(response.getWriter());
        bufferedWriter.write(PageGenerator.instance().getPage("index.html", pageVariables));
        bufferedWriter.flush();
    }
}
