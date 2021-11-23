package com.zmops.zeus.iot.server.receiver.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

/**
 * @author nantian created at 2021/10/26 0:28
 */
public class PgEventRouteBuilder extends RouteBuilder {


    @Override
    public void configure() throws Exception {
//        from("pgevent:172.16.60.99:5432/zabbix/zabbix_pg_event?pass=postgres&user=postgres")
//                .log(LoggingLevel.DEBUG, log, ">>> PgEvent received from Zabbix Events : ${body}");
    }
}
