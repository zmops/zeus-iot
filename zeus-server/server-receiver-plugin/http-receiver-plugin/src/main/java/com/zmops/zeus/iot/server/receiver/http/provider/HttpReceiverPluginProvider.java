package com.zmops.zeus.iot.server.receiver.http.provider;

import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.receiver.http.module.HttpReceiverModule;
import com.zmops.zeus.iot.server.sender.module.ZabbixSenderModule;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

/**
 * @author nantian created at 2021/8/14 22:29
 */
public class HttpReceiverPluginProvider extends ModuleProvider {

    private final HttpReceiverConfig httpReceiverConfig;

    public HttpReceiverPluginProvider() {
        this.httpReceiverConfig = new HttpReceiverConfig();
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return HttpReceiverModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return httpReceiverConfig;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        ModelCamelContext camelContext = new DefaultCamelContext();
        try {
            camelContext.start();
            camelContext.addRoutes(new HttpRouteBuilder(httpReceiverConfig, getManager()));
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
                ZabbixSenderModule.NAME
        };
    }
}
