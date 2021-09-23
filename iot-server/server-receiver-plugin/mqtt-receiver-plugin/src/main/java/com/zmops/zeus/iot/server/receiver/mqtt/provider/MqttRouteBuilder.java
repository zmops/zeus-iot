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
        fromF("mqtt:zeus-iot-mqtt?host=tcp://%s:%d&subscribeTopicNames=34EAE784BC4A/up", mqttReceiverConfig.getHost(),mqttReceiverConfig.getPort())
                .process(new ByteArrayToItemValueProcess())
                .to("Zabbix");
    }
}
