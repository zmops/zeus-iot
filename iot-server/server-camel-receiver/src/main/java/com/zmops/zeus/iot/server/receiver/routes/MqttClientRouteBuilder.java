package com.zmops.zeus.iot.server.receiver.routes;

import com.zmops.zeus.iot.server.h2.module.LocalH2Module;
import com.zmops.zeus.iot.server.h2.service.InsertDAO;
import com.zmops.zeus.iot.server.receiver.ReceiverServerRoute;
import com.zmops.zeus.server.library.module.ModuleManager;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/10/24 0:11
 * <p>
 * mqtt client
 */
public class MqttClientRouteBuilder extends ReceiverServerRoute {

    public MqttClientRouteBuilder(String routeId, Map<String, Object> options) {
        super(routeId, options);
    }

    @Override
    public void configure() throws Exception {
        fromF("paho-mqtt5:%s?brokerUrl=tcp://%s:%s", options.get("topicNames"), options.get("hostIp"), options.get("port"))
                .routeId(routeId)
                .log(LoggingLevel.DEBUG, log, ">>> Message received from Mqtt Client : \n${body}")
                .dynamicRouter(method(RouteJudge.class, "slip")).to("Zabbix:mqtt");
    }


    static class RouteJudge {
        public static String slip(Exchange exchange) {
            Message message = exchange.getMessage();

            if (message.getBody() instanceof List) {
                return null;
            }

            String topicName = exchange.getIn().getHeader("CamelMqttTopic").toString();
            InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                    .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);
            //TODO 这里需要动态加载规则
            ResultSet rs = localH2InsertDAO.queryRes("select pm.topic,pc.unique_id from protocol_gateway_mqtt pm left join protocol_component pc on pc.id=pm.protocol_component_id");
            Map<String, String> map = new HashMap<>();
            while (true) {
                try {
                    if (!rs.next()) {
                        break;
                    }

                    map.put(rs.getString(1), rs.getString(2));

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (map.containsKey(topicName)) {
                return "ArkBiz:mqtt?uniqueId=" + map.get(topicName);
            }

//            if ("zeus11".equals(topicName)) {
//                return "ArkBiz:mqtt?uniqueId=19890918";
//            }
//
//            if ("zeus22".equals(topicName)) {
//                return "ArkBiz:mqtt?uniqueId=20211225";
//            }

            return null;
        }

    }
}
