package com.zmops.zeus.iot.server.receiver.mqtt.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * @author yefei
 */
public class MqttPacketProcess implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getMessage());
    }
}
