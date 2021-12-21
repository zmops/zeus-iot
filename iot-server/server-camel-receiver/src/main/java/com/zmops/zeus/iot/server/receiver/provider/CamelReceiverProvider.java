package com.zmops.zeus.iot.server.receiver.provider;

import com.zmops.zeus.iot.server.receiver.Const;
import com.zmops.zeus.iot.server.receiver.handler.ark.ArkBizComponent;
import com.zmops.zeus.iot.server.receiver.handler.zabbix.ZabbixSenderComponent;
import com.zmops.zeus.iot.server.receiver.module.CamelReceiverModule;
import com.zmops.zeus.iot.server.receiver.service.CamelContextHolderService;
import com.zmops.zeus.iot.server.receiver.service.ReferenceClientService;
import com.zmops.zeus.iot.server.sender.module.ZabbixSenderModule;
import com.zmops.zeus.server.library.module.*;
import com.zmops.zeus.server.runtime.SofaStarter;
import com.zmops.zeus.server.runtime.api.client.ReferenceClient;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.PooledExchangeFactory;
import org.apache.camel.model.ModelCamelContext;

/**
 * @author nantian created at 2021/10/23 21:24
 */
public class CamelReceiverProvider extends ModuleProvider {

    private final CamelReceiverConfig camelReceiverConfig;

    private ModelCamelContext camelContext;

    private ReferenceClient referenceClient;

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
        camelContext = new DefaultCamelContext(); // master 只有一个 CamelContext
        camelContext.disableJMX();
        camelContext.setTracing(true);
        camelContext.adapt(ExtendedCamelContext.class).setExchangeFactory(new PooledExchangeFactory());

        // biz-ark JVM 通信客户端
        SofaStarter sofaStarter = new SofaStarter();
        this.referenceClient = sofaStarter.getSofaRuntimeContext().getClientFactory().getClient(ReferenceClient.class);

        this.registerServiceImplementation(CamelContextHolderService.class, new CamelContextHolderService(camelContext, getManager()));
        this.registerServiceImplementation(ReferenceClientService.class, new ReferenceClientService(referenceClient, getManager()));
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        camelContext.addComponent(Const.CAMEL_ZABBIX_COMPONENT_NAME, new ZabbixSenderComponent(getManager()));
        camelContext.addComponent(Const.CAMEL_ARK_COMPONENT_NAME, new ArkBizComponent(getManager()));
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {
        try {
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                ZabbixSenderModule.NAME
        };
    }
}
