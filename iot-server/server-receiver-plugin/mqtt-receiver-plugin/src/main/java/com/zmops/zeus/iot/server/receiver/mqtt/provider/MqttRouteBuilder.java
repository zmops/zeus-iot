package com.zmops.zeus.iot.server.receiver.mqtt.provider;

import com.zmops.zeus.iot.server.core.camel.process.ByteArrayToItemValueProcess;
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
        fromF("mqtt:zeus-iot-mqtt?host=tcp://172.16.3.72:%d&subscribeTopicNames=test.mqtt.topic", mqttReceiverConfig.getPort())
                .process(new ByteArrayToItemValueProcess())
                .to("Zabbix");
    }
}
