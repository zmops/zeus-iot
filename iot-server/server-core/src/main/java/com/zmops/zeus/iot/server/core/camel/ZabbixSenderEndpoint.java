package com.zmops.zeus.iot.server.core.camel;

import com.zmops.zeus.iot.server.library.module.ModuleManager;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * @author nantian created at 2021/8/16 22:45
 */
public class ZabbixSenderEndpoint extends DefaultEndpoint {

    private final ModuleManager moduleManager;
    private final ZabbixTrapperProducer producer;

    public ZabbixSenderEndpoint(String endpointUri, Component component, ModuleManager moduleManager) {
        super(endpointUri, component);
        this.moduleManager = moduleManager;
        this.producer = new ZabbixTrapperProducer(this, moduleManager);
    }

    @Override
    public Producer createProducer() throws Exception {
        return producer; // 每次 process 都会创建一个对象
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new ZabbixSenderConsumer(this, processor); //TODO
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
