package com.zmops.zeus.iot.web.controller;

import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.api.ClientResponse;
import com.google.gson.Gson;
import com.zmops.zeus.iot.server.receiver.ProtocolAction;
import com.zmops.zeus.iot.server.receiver.ProtocolEnum;
import com.zmops.zeus.iot.server.receiver.module.CamelReceiverModule;
import com.zmops.zeus.iot.server.receiver.routes.*;
import com.zmops.zeus.iot.server.receiver.service.CamelContextHolderService;
import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.library.web.core.Controller;
import com.zmops.zeus.server.library.web.core.Path;
import com.zmops.zeus.server.library.web.upload.UploadFile;
import com.zmops.zeus.server.library.web.util.HttpKit;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author nantian created at 2021/11/23 23:24
 */

@Path(value = "/protocol", viewPath = "/")
public class IndexController extends Controller {


    private final Gson gson = new Gson();

    public void upload() {
        UploadFile file = getFile();

        try {
            String filePath = file.getFile().getCanonicalPath();

            renderJson("filePath", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void installArk() {

        String fileName = getPara("fileName");

        try {
//            ClientResponse response = ArkClient.installBiz(new File("D:\\666\\zeus-ark-hello2-1.0-beta-ark-biz.jar"));
//            ClientResponse response1 = ArkClient.checkBiz("zeus-ark-hello", "1.0-beta");

//            ClientResponse response = ArkClient.installBiz(new File("D:\\666\\upload\\" + fileName));
            ClientResponse response = ArkClient.installBiz(new File("//opt//zeus//zeus-iot-bin//upload//" + fileName));

            renderJson(response);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void uninstallArk() {
        try {
            ClientResponse response = ArkClient.uninstallBiz("zeus-ark-hello", "1.0-beta");
            renderJson(response);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public void stopRoute() {

        String routeId = getPara("routeId");

        CamelContextHolderService camelContextHolderService = ModuleManager.getInstance()
                .find(CamelReceiverModule.NAME).provider().getService(CamelContextHolderService.class);

        camelContextHolderService.routeShutDown(routeId);

        renderNull();

    }


    public void startRoute() {

        String routeId = getPara("routeId");

        CamelContextHolderService camelContextHolderService = ModuleManager.getInstance()
                .find(CamelReceiverModule.NAME).provider().getService(CamelContextHolderService.class);


        camelContextHolderService.routeStartUp(routeId);

        renderNull();
    }


    public void createRoute() {


        String json = HttpKit.readData(getRequest());


        ProtocolService protocol = gson.fromJson(json, ProtocolService.class);
        String routeId = protocol.getRouteId();
        Map<String, Object> options = protocol.getOptions();

//        SofaStarter sofaStarter = new SofaStarter();
//        ReferenceClient referenceClient = sofaStarter.getSofaRuntimeContext().getClientFactory().getClient(ReferenceClient.class);
//
//        ReferenceParam<SayHelloFacade> referenceParam = new ReferenceParam<>();
//        referenceParam.setInterfaceType(SayHelloFacade.class);
//        referenceParam.setUniqueId("198909118");
//        SayHelloFacade facade = referenceClient.reference(referenceParam);
//
//        facade.hello(" zmops is great !!!! ");

        CamelContextHolderService camelContextHolderService = ModuleManager.getInstance()
                .find(CamelReceiverModule.NAME).provider().getService(CamelContextHolderService.class);


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
            camelContextHolderService.addRoutes(route);
        }


        renderNull();
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
