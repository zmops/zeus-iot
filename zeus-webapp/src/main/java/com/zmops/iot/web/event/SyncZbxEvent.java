package com.zmops.iot.web.event;

import com.zmops.iot.util.SpringUtils;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author yefei
 **/
@Component
public class SyncZbxEvent extends RouteBuilder {

    @Value("${forest.variables.zbxServerIp}")
    public String host;

    @Override
    public void configure() throws Exception {
        fromF("pgevent:%s:5432/zabbix/zabbix_pg_event?pass=postgres&user=postgres", host)
                .process(SpringUtils.getBean(EventDataProcess.class))

                .log(LoggingLevel.DEBUG, log, ">>> PgEvent received from Zabbix Events : ${body}");
    }

}
