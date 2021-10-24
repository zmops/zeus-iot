package com.zmops.zeus.iot.server.receiver.routes;

import com.zmops.zeus.iot.server.receiver.ReceiverServerRoute;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;

import java.util.Map;

/**
 * @author nantian created at 2021/10/23 23:25
 */

@Slf4j
public class HttpRouteBuilder extends ReceiverServerRoute {

    public HttpRouteBuilder(String routeId, Map<String, Object> option) {
        super(routeId, option);
        log.info("Http Route Created ====> ip : {}，port : {}", option.get("hostIp"), option.get("port"));
    }

    @Override
    public void configure() throws Exception {
        fromF("netty4-http:http://0.0.0.0:%s/data?sync=true", options.get("port"))
                .routeId(routeId).log(LoggingLevel.DEBUG, log, ">>> Message received from Netty4 Http Server : ${body}");
    }


}
