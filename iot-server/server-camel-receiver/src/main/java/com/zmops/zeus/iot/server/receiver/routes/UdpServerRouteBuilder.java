package com.zmops.zeus.iot.server.receiver.routes;

import com.zmops.zeus.iot.server.receiver.ReceiverServerRoute;

import java.util.Map;

/**
 * @author nantian created at 2021/10/24 0:12
 */
public class UdpServerRouteBuilder extends ReceiverServerRoute {

    public UdpServerRouteBuilder(String routeId, Map<String, Object> options) {
        super(routeId, options);
    }

    @Override
    public void configure() throws Exception {

    }
}
