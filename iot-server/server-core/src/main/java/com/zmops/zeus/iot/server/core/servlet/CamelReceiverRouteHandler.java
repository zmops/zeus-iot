package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.JsonElement;
import com.zmops.zeus.iot.server.library.server.jetty.ArgumentsParseException;
import com.zmops.zeus.iot.server.library.server.jetty.JettyJsonHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author nantian created at 2021/10/23 21:38
 * <p>
 * 动态创建 Camel 路由 Jetty 入口
 */
public class CamelReceiverRouteHandler extends JettyJsonHandler {

    @Override
    public String pathSpec() {
        return "/camel/route";
    }

    @Override
    protected JsonElement doPost(HttpServletRequest req) throws ArgumentsParseException, IOException {
        return null;
    }
}
