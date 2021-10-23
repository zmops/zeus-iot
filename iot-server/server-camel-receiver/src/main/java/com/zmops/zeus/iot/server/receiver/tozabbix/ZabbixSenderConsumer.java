package com.zmops.zeus.iot.server.receiver.tozabbix;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * @author nantian created at 2021/8/16 22:47
 */
public class ZabbixSenderConsumer extends DefaultConsumer {

    public ZabbixSenderConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
}
