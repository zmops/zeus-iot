package com.zmops.zeus.iot.server.sender.provider;

import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.sender.module.ZabbixSenderModule;
import com.zmops.zeus.iot.server.sender.service.ZabbixSenderService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nantian created at 2021/8/14 14:40
 */
@Slf4j
public class ZabbixSenderProvider extends ModuleProvider {

    private final ZabbixSenderModuleConfig senderConfig;

    public ZabbixSenderProvider() {
        this.senderConfig = new ZabbixSenderModuleConfig();
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return ZabbixSenderModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return senderConfig;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {
        this.registerServiceImplementation(ZabbixSenderService.class, new ZabbixSenderService(getManager()));
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        ZabbixSenderClient senderClient = new ZabbixSenderClient(senderConfig);
        senderClient.start();
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public String[] requiredModules() {
        return new String[0];
    }
}
