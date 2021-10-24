package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.zmops.zeus.iot.server.library.module.ModuleManager;
import com.zmops.zeus.iot.server.library.server.jetty.ArgumentsParseException;
import com.zmops.zeus.iot.server.library.server.jetty.JettyJsonHandler;
import com.zmops.zeus.iot.server.receiver.ProtocolAction;
import com.zmops.zeus.iot.server.receiver.ProtocolEnum;
import com.zmops.zeus.iot.server.receiver.module.CamelReceiverModule;
import com.zmops.zeus.iot.server.receiver.routes.*;
import com.zmops.zeus.iot.server.receiver.service.CamelContextHolderService;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author nantian created at 2021/10/23 21:38
 * <p>
 * 动态创建 Camel 路由 Jetty 入口
 */
public class CamelReceiverRouteHandler extends JettyJsonHandler {

    private final ModuleManager moduleManager;

    private final Gson gson = new Gson();

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
        ProtocolService protocol = gson.fromJson(request, ProtocolService.class);

        String routeId = protocol.getRouteId();
        Map<String, Object> options = protocol.getOptions();

        if (protocol.getAction() != null) {
            switch (protocol.getAction()) {
                case start:
                    camelService.routeStartUp(routeId);
                    break;
                case stop:
                    camelService.routeShutDown(routeId);
                    break;
                default:
                    break;
            }

            return null;
        }


        RouteBuilder route = null;
        switch (protocol.getProtocol()) {
            case HttpServer:
                route = new HttpRouteBuilder(routeId, options);
                break;
            case MqttClient:
                route = new MqttClientRouteBuilder(routeId, options);
                break;
            case TcpServer:
                route = new TcpServerRouteBuilder(routeId, options);
                break;
            case UdpServer:
                route = new UdpServerRouteBuilder(routeId, options);
                break;
            case CoapServer:
                route = new CoapServerRouteBuilder(routeId, options);
                break;
            case WebSocketServer:
                route = new WebSocketServerRouteBuilder(routeId, options);
                break;
            default:
                break;
        }

        if (route != null) {
            camelService.addRoutes(route);
        }

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

    @Getter
    @Setter
    static class ProtocolService {

        private String routeId;

        private ProtocolEnum protocol;

        private ProtocolAction action;

        private Map<String, Object> options;
    }
}
