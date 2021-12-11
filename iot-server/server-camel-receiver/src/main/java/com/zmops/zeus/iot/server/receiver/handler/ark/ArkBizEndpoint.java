package com.zmops.zeus.iot.server.receiver.handler.ark;

import com.zmops.zeus.server.library.module.ModuleManager;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;


import org.apache.camel.support.DefaultEndpoint;

/**
 * @author nantian created at 2021/12/2 22:34
 */
@SuppressWarnings("all")
public class ArkBizEndpoint extends DefaultEndpoint {

    private final ModuleManager moduleManager;
    private final ArkBizProducer producer;

    public ArkBizEndpoint(String endpointUri, Component component, ModuleManager moduleManager,
                          String uniqueId, String methodName) {
        super(endpointUri, component);
        this.moduleManager = moduleManager;
        this.producer = new ArkBizProducer(this, moduleManager, uniqueId, methodName);
    }


    @Override
    public Producer createProducer() throws Exception {
        return this.producer;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new ArkBizConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
