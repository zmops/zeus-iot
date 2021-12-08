package com.zmops.zeus.iot.server.receiver.handler.ark;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * @author nantian created at 2021/12/2 22:35
 */
public class ArkBizConsumer extends DefaultConsumer {

    public ArkBizConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
}
