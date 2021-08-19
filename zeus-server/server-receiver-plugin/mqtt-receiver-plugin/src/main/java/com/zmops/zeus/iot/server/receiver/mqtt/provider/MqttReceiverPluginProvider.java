package com.zmops.zeus.iot.server.receiver.mqtt.provider;

import com.zmops.zeus.iot.server.core.CoreModule;
import com.zmops.zeus.iot.server.core.camel.CamelContextHolderService;
import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.receiver.mqtt.module.MqttReceiverModule;

/**
 * @author yefei
 */
public class MqttReceiverPluginProvider extends ModuleProvider {

    private final MqttReceiverConfig mqttReceiverConfig;

    private CamelContextHolderService camelService;

    public MqttReceiverPluginProvider() {
        this.mqttReceiverConfig = new MqttReceiverConfig();
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return MqttReceiverModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return mqttReceiverConfig;
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
        camelService.addRoutes(new MqttRouteBuilder(mqttReceiverConfig));
    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                CoreModule.NAME
        };
    }
}
