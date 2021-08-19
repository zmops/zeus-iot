package com.zmops.zeus.iot.server.core;

import com.zmops.zeus.iot.server.core.camel.CamelContextHolderService;
import com.zmops.zeus.iot.server.core.camel.ZabbixSenderComponent;
import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.sender.module.ZabbixSenderModule;
import com.zmops.zeus.iot.server.telemetry.TelemetryModule;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

/**
 * @author nantian created at 2021/8/16 22:26
 */
public class CoreModuleProvider extends ModuleProvider {

    private final CoreModuleConfig  moduleConfig;
    private       ModelCamelContext camelContext;


    public CoreModuleProvider() {
        super();
        this.moduleConfig = new CoreModuleConfig();
    }


    @Override
    public String name() {
        return "default";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return CoreModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return moduleConfig;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {
        camelContext = new DefaultCamelContext();
        camelContext.addComponent(Const.CAMEL_ZABBIX_COMPONENT_NAME, new ZabbixSenderComponent(getManager()));

        this.registerServiceImplementation(CamelContextHolderService.class, new CamelContextHolderService(camelContext, getManager()));
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        try {
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                TelemetryModule.NAME,
                ZabbixSenderModule.NAME
        };
    }
}
