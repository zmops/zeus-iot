package com.zmops.zeus.iot.server.receiver.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;

/**
 * @author nantian created at 2021/10/23 23:25
 */

@Slf4j
public class HttpRouteBuilder extends RouteBuilder {

    private final String  hostIp;
    private final Integer port;

    public HttpRouteBuilder(String hostIp, Integer port) {
        this.hostIp = hostIp;
        this.port = port;

        log.info("Http Route Created ====> ip : {}，port : {}", hostIp, port);
    }

    @Override
    public void configure() throws Exception {
        fromF("netty4-http:http://0.0.0.0:%d/data?sync=true", port).log(">>> Message received from Netty4 Http Server : ${body}");
    }
}
