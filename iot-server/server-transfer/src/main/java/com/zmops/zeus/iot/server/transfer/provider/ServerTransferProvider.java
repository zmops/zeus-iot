package com.zmops.zeus.iot.server.transfer.provider;

import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.transfer.module.ServerTransferModule;

/**
 * @author nantian created at 2021/9/22 16:44
 */
public class ServerTransferProvider extends ModuleProvider {

    private final ServerTransferConfig serverTransferConfig;

    public ServerTransferProvider() {
        this.serverTransferConfig = new ServerTransferConfig();
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return ServerTransferModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return serverTransferConfig;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {
        System.out.println(123);
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public String[] requiredModules() {
        return new String[0];
    }
}
