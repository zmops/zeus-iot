package com.zmops.zeus.iot.server.core.servlet;

import com.zmops.zeus.iot.server.library.server.jetty.JettyHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nantian created at 2021/8/20 11:38
 */
public class HostInfoServletHandler extends JettyHandler {


    @Override
    public String pathSpec() {
        return "/monitor/hostinfo";
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.getWriter().write("Helloworld");
    }
}
