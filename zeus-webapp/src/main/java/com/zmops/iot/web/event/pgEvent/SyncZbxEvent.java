package com.zmops.iot.web.event.pgEvent;

import com.zmops.iot.util.SpringUtils;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author yefei
 **/
@Component
public class SyncZbxEvent extends RouteBuilder {

    @Value("${spring.datasource.druid.url}")
    private String url;
    @Value("${spring.datasource.druid.username}")
    private String username;
    @Value("${spring.datasource.druid.password}")
    private String password;

    @Override
    public void configure() throws Exception {
        url = url.substring(url.indexOf("//") + 2);
        String host = url.substring(0, url.indexOf(":"));
        int port = Optional.of(url.substring(url.indexOf(":") + 1, url.indexOf("/"))).map(Integer::parseInt).orElse(5432);

        fromF("pgevent:%s:%d/zabbix/zabbix_pg_event?pass=%s&user=%s", host, port, password, username)
                .process(SpringUtils.getBean(EventDataProcess.class))

                .log(LoggingLevel.DEBUG, log, ">>> PgEvent received from Zabbix Events : ${body}");
    }

}
