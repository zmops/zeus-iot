package com.zmops.zeus.iot.server.receiver.routes;

import com.zmops.zeus.iot.server.receiver.ReceiverServerRoute;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;

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
        fromF("mqtt:zeus-iot-mqtt?host=tcp://%s:%s&subscribeTopicNames=%s", options.get("hostIp"), options.get("port"), options.get("topicNames"))
                .routeId(routeId)
                .log(LoggingLevel.DEBUG, log, ">>> Message received from Mqtt Client : \n${body}")
                .dynamicRouter(method(RouteJudge.class, "slip")).to("Zabbix");
    }


    static class RouteJudge {
        public static String slip(Exchange exchange) {
            Message message = exchange.getMessage();

            if (message.getBody() instanceof List) {
                return null;
            }

            String topicName = exchange.getIn().getHeader("CamelMQTTSubscribeTopic").toString();

            //TODO 这里需要动态加载规则

            if (topicName.equals("zeusiot/123")) {
                return "ArkBiz?uniqueId=198909118";
            }

            if (topicName.equals("zeusiot/1234")) {
                return "ArkBiz?uniqueId=19890918";
            }

            return null;
        }

    }
}
