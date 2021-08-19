package com.zmops.zeus.iot.server.receiver.mqtt.provider;

import com.zmops.zeus.iot.server.receiver.mqtt.process.MqttPacketProcess;
import org.apache.camel.builder.RouteBuilder;

/**
 * @author yefei
 */
public class MqttRouteBuilder extends RouteBuilder {


    private final MqttReceiverConfig mqttReceiverConfig;

    public MqttRouteBuilder(MqttReceiverConfig mqttReceiverConfig) {
        this.mqttReceiverConfig = mqttReceiverConfig;
    }

    @Override
    public void configure() throws Exception {
        fromF("mqtt://127.0.0.1:%d?subscribeTopicNames=test.mqtt.topic", mqttReceiverConfig.getPort())
                .process(new MqttPacketProcess())
                .to("Zabbix");
    }
}
