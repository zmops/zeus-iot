package com.zmops.zeus.iot.server.receiver.routes;

import com.zmops.zeus.iot.server.receiver.ReceiverServerRoute;

import java.util.Map;

/**
 * @author nantian created at 2021/11/26 13:54
 */
public class MqttBrokerRouteBuilder extends ReceiverServerRoute {

    public MqttBrokerRouteBuilder(String routeId, Map<String, Object> options) {
        super(routeId, options);
    }

    @Override
    public void configure() throws Exception {

    }
}
