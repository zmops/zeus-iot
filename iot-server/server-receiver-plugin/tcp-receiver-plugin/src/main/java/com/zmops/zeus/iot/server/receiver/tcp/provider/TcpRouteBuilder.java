package com.zmops.zeus.iot.server.receiver.tcp.provider;

import com.zmops.zeus.iot.server.core.camel.process.ByteArrayToItemValueProcess;
import com.zmops.zeus.iot.server.core.camel.process.StringItemValueProcess;
import org.apache.camel.builder.RouteBuilder;

/**
 * @author nantian created at 2021/8/17 11:32
 */
public class TcpRouteBuilder extends RouteBuilder {


    private TcpReceiverConfig tcpReceiverConfig;

    public TcpRouteBuilder(TcpReceiverConfig tcpReceiverConfig) {
        this.tcpReceiverConfig = tcpReceiverConfig;
    }

    @Override
    public void configure() throws Exception {
        fromF("netty4:tcp://0.0.0.0:%d", tcpReceiverConfig.getPort())
                .process(new StringItemValueProcess())
                .to("Zabbix");
    }
}
