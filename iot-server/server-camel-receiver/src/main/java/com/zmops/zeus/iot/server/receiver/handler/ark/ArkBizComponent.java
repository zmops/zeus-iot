package com.zmops.zeus.iot.server.receiver.handler.ark;

import com.zmops.zeus.server.library.module.ModuleManager;
import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

/**
 * @author nantian created at 2021/12/2 22:35
 */
public class ArkBizComponent extends DefaultComponent {

    private final ModuleManager moduleManager;

    public ArkBizComponent(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        String uniqueId = getAndRemoveParameter(parameters, "uniqueId", String.class);
        String methodName = getAndRemoveParameter(parameters, "methodName", String.class);

        return new ArkBizEndpoint(uri, this, moduleManager, uniqueId, methodName);
    }
}
