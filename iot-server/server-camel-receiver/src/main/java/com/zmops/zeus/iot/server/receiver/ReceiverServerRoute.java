package com.zmops.zeus.iot.server.receiver;

import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;

import java.util.Map;

/**
 * @author nantian created at 2021/10/24 14:38
 */
public abstract class ReceiverServerRoute extends RouteBuilder {

    @Setter
    protected String routeId;

    @Setter
    protected Map<String, Object> options;

    public ReceiverServerRoute(String routeId, Map<String, Object> options) {
        this.routeId = routeId;
        this.options = options;
    }
}
