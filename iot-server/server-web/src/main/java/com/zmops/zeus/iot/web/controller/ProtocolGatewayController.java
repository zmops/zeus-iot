package com.zmops.zeus.iot.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.zeus.iot.server.h2.module.LocalH2Module;
import com.zmops.zeus.iot.server.h2.service.InsertDAO;
import com.zmops.zeus.iot.server.receiver.ProtocolAction;
import com.zmops.zeus.iot.server.receiver.ProtocolEnum;
import com.zmops.zeus.iot.server.receiver.module.CamelReceiverModule;
import com.zmops.zeus.iot.server.receiver.routes.*;
import com.zmops.zeus.iot.server.receiver.service.CamelContextHolderService;
import com.zmops.zeus.iot.web.domain.ProtocolGatewayMqtt;
import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.library.web.core.Controller;
import com.zmops.zeus.server.library.web.core.Path;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/11/23 23:24
 */

@Path(value = "/protocol/gateway")
public class ProtocolGatewayController extends Controller {

    public void stopRoute() {

        String routeId = getPara("routeId");

        stopGateway(routeId);

        renderNull();

    }

    public void stopGateway(String routeId) {

        CamelContextHolderService camelContextHolderService = ModuleManager.getInstance()
                .find(CamelReceiverModule.NAME).provider().getService(CamelContextHolderService.class);

        camelContextHolderService.routeShutDown(routeId);

        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);

        localH2InsertDAO.update("update protocol_gateway set status=1 where id=?", routeId);

    }


    public void startRoute() {

        String routeId = getPara("routeId");

        CamelContextHolderService camelContextHolderService = ModuleManager.getInstance()
                .find(CamelReceiverModule.NAME).provider().getService(CamelContextHolderService.class);

        camelContextHolderService.routeStartUp(routeId);


        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);

        localH2InsertDAO.update("update protocol_gateway set status=0 where id=?", routeId);

        renderNull();
    }

    public void createProtocolGateway() {
        String routeId = getPara("routeId");
        String name = getPara("name");
        String protocolServiceId = getPara("protocolServiceId");
        String protocolComponentId = getPara("protocolComponentId");
        String status = getPara("status");
        String protocol = getPara("protocol");
        String option = getPara("option");
        String mqttList = getPara("mqttList");

        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);
        localH2InsertDAO.insert("insert into protocol_gateway(id,name,protocol_service_id,protocol_component_id,status) values(" + routeId + ",'" + name + "'," + protocolServiceId + "," + protocolComponentId + ",'" + status  + "')");

        if (StringUtils.isNotBlank(mqttList)) {
            saveMqttList(mqttList);
        }
        Map<String, Object> options = JSON.parseObject(option, Map.class);
        createRoute(routeId, ProtocolEnum.valueOf(protocol), options);
    }

    public void updateProtocolGateway() throws SQLException {
        String routeId = getPara("routeId");
        stopGateway(routeId);

        String name = getPara("name");
        String protocolServiceId = getPara("protocolServiceId");
        String protocolComponentId = getPara("protocolComponentId");
        String remark = getPara("remark");
        String option = getPara("option");
        String mqttList = getPara("mqttList");

        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);

        StringBuilder sql = new StringBuilder("update protocol_gateway set protocol_service_id=?");
        if (StringUtils.isNotBlank(name)) {
            sql.append(" ,name =?");
        }
        if (StringUtils.isNotBlank(protocolComponentId)) {
            sql.append(" ,protocolComponentId =?");
        }
        if (StringUtils.isNotBlank(remark)) {
            sql.append(" ,remark =?");
        }
        sql.append(" where id=?");

        localH2InsertDAO.update("", protocolServiceId, name, protocolComponentId, remark, routeId);

        localH2InsertDAO.delete("delete from protocol_gateway_mqtt where protocol_gateway_id = " + routeId);
        if (StringUtils.isNotBlank(mqttList)) {
            saveMqttList(mqttList);
        }
        Map<String, Object> options = JSON.parseObject(option, Map.class);

        ResultSet rs = localH2InsertDAO.queryRes("select * from protocol_service where id=?", protocolServiceId);
        String protocol = "";
        while (rs.next()) {
            protocol = rs.getString(9);
        }
        createRoute(routeId, ProtocolEnum.valueOf(protocol), options);
    }

    public void createRoute(String routeId, ProtocolEnum protocol, Map<String, Object> options) {

        CamelContextHolderService camelContextHolderService = ModuleManager.getInstance()
                .find(CamelReceiverModule.NAME).provider().getService(CamelContextHolderService.class);

        RouteBuilder route = null;
        switch (protocol) {
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

    private void saveMqttList(String mqttList) {
        List<ProtocolGatewayMqtt> protocolGatewayMqtts = JSONObject.parseArray(mqttList, ProtocolGatewayMqtt.class);
        if (protocolGatewayMqtts == null || protocolGatewayMqtts.size() <= 0) {
            return;
        }
        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);
        protocolGatewayMqtts.forEach(mqtt -> {
            localH2InsertDAO.insert("insert into protocol_gateway_mqtt(topic,protocol_component_id,protocol_gateway_id) values('" + mqtt.getTopic() + "'," + mqtt.getProtocolComponentId() + "," + mqtt.getProtocolGatewayId() + ")");
        });
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
