package com.zmops.zeus.iot.server.receiver.tcp.provider;

import com.zmops.zeus.iot.server.core.CoreModule;
import com.zmops.zeus.iot.server.core.camel.CamelContextHolderService;
import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.receiver.tcp.module.TcpReceiverModule;

/**
 * @author nantian created at 2021/8/14 22:29
 */
public class TcpReceiverPluginProvider extends ModuleProvider {

    private final TcpReceiverConfig tcpReceiverConfig;

    // CamelContextHolder
    private CamelContextHolderService camelService;

    public TcpReceiverPluginProvider() {
        this.tcpReceiverConfig = new TcpReceiverConfig();
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return TcpReceiverModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return tcpReceiverConfig;
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
        camelService.addRoutes(new TcpRouteBuilder(tcpReceiverConfig));
    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                CoreModule.NAME
        };
    }
}
