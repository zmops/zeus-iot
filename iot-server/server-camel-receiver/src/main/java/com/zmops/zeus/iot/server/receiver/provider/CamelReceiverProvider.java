package com.zmops.zeus.iot.server.receiver.provider;

import com.zmops.zeus.iot.server.core.CoreModule;
import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.receiver.Const;
import com.zmops.zeus.iot.server.receiver.module.CamelReceiverModule;
import com.zmops.zeus.iot.server.receiver.service.CamelContextHolderService;
import com.zmops.zeus.iot.server.receiver.tozabbix.ZabbixSenderComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

/**
 * @author nantian created at 2021/10/23 21:24
 */
public class CamelReceiverProvider extends ModuleProvider {

    private final CamelReceiverConfig camelReceiverConfig;

    private ModelCamelContext camelContext;

    public CamelReceiverProvider() {
        this.camelReceiverConfig = new CamelReceiverConfig();
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return CamelReceiverModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return camelReceiverConfig;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {
        camelContext = new DefaultCamelContext();
        camelContext.addComponent(Const.CAMEL_ZABBIX_COMPONENT_NAME, new ZabbixSenderComponent(getManager()));
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        try {
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.registerServiceImplementation(CamelContextHolderService.class, new CamelContextHolderService(camelContext, getManager()));
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                CoreModule.NAME
        };
    }
}
