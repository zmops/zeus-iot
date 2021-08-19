package com.zmops.zeus.iot.server.receiver.http.provider;

import com.zmops.zeus.iot.server.core.CoreModule;
import com.zmops.zeus.iot.server.core.camel.CamelContextHolderService;
import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.receiver.http.module.HttpReceiverModule;

/**
 * @author nantian created at 2021/8/14 22:29
 */
public class HttpReceiverPluginProvider extends ModuleProvider {

    private final HttpReceiverConfig httpReceiverConfig;

    // CamelContextHolder
    private CamelContextHolderService camelService;

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
        camelService = getManager().find(CoreModule.NAME).provider().getService(CamelContextHolderService.class);
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {
        camelService.addRoutes(new HttpRouteBuilder(httpReceiverConfig));
    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                CoreModule.NAME
        };
    }
}
