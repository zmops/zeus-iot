package com.zmops.zeus.iot.server.receiver.tcp.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * @author nantian created at 2021/8/17 11:36
 */
public class TcpPacketProcess implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        // 处理协议，想怎么写 就怎么写, 塞回 Body 最后 to("Zabbix")，show time !!!

    }
}
