package com.zmops.zeus.iot.server.receiver.http.provider;

import com.zmops.zeus.iot.server.receiver.http.predicate.HeaderPredicate;
import org.apache.camel.builder.RouteBuilder;

/**
 * @author nantian created at 2021/8/14 22:41
 */
public class HttpRouteBuilder extends RouteBuilder {

    private final HttpReceiverConfig config;

    public HttpRouteBuilder(HttpReceiverConfig config) {
        this.config = config;
    }

    @Override
    public void configure() throws Exception {
        fromF("netty4-http:http://0.0.0.0:%d/foo", config.getPort())
                .threads(10)
                .choice()
                .when(new HeaderPredicate())
                .to("Zabbix");
    }
}
