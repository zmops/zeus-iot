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

            //TODO 这里需要动态加载规则

            if ("zeus11".equals(topicName)) {
                return "ArkBiz:mqtt?uniqueId=19890918";
            }

            if ("zeus22".equals(topicName)) {
                return "ArkBiz:mqtt?uniqueId=20211225";
            }

            return null;
        }

    }
}
