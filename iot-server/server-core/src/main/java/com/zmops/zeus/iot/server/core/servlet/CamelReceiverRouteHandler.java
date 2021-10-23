package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.JsonElement;
import com.zmops.zeus.iot.server.library.module.ModuleManager;
import com.zmops.zeus.iot.server.library.server.jetty.ArgumentsParseException;
import com.zmops.zeus.iot.server.library.server.jetty.JettyJsonHandler;
import com.zmops.zeus.iot.server.receiver.module.CamelReceiverModule;
import com.zmops.zeus.iot.server.receiver.routes.HttpRouteBuilder;
import com.zmops.zeus.iot.server.receiver.service.CamelContextHolderService;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author nantian created at 2021/10/23 21:38
 * <p>
 * 动态创建 Camel 路由 Jetty 入口
 */
public class CamelReceiverRouteHandler extends JettyJsonHandler {

    private final ModuleManager moduleManager;

    public CamelReceiverRouteHandler(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public String pathSpec() {
        return "/camel/route";
    }

    @Override
    protected JsonElement doPost(HttpServletRequest req) throws ArgumentsParseException, IOException {

        CamelContextHolderService camelService = moduleManager.find(CamelReceiverModule.NAME).provider().getService(CamelContextHolderService.class);

        String request = getJsonBody(req);

        camelService.addRoutes(new HttpRouteBuilder("0.0.0.0", Integer.valueOf(request)));

        return null;
    }


    public String getJsonBody(HttpServletRequest req) throws IOException {
        StringBuilder stringBuffer = new StringBuilder();
        String line = null;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }
}
