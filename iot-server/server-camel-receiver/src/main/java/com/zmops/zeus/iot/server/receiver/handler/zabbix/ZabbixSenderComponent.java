package com.zmops.zeus.iot.server.receiver.handler.zabbix;

import com.zmops.zeus.server.library.module.ModuleManager;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

/**
 * @author nantian created at 2021/8/16 22:38
 */
public class ZabbixSenderComponent extends DefaultComponent {

    private final ModuleManager moduleManager;

    public ZabbixSenderComponent(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        // parameters 里面的参数 必须要清空
        String value = getAndRemoveParameter(parameters, "method", String.class);

        return new ZabbixSenderEndpoint(uri, this, moduleManager);
    }
}
