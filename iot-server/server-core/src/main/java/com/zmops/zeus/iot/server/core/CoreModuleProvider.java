package com.zmops.zeus.iot.server.core;

import com.zmops.zeus.iot.server.core.camel.CamelContextHolderService;
import com.zmops.zeus.iot.server.core.camel.ZabbixSenderComponent;
import com.zmops.zeus.iot.server.core.server.JettyHandlerRegister;
import com.zmops.zeus.iot.server.core.server.JettyHandlerRegisterImpl;
import com.zmops.zeus.iot.server.core.servlet.HttpItemTrapperHandler;
import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.library.server.jetty.JettyServer;
import com.zmops.zeus.iot.server.library.server.jetty.JettyServerConfig;
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

    private JettyServer jettyServer;


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

        JettyServerConfig jettyServerConfig = JettyServerConfig.builder()
                .host(moduleConfig.getRestHost())
                .port(moduleConfig.getRestPort())
                .contextPath(moduleConfig.getRestContextPath())
                .jettyIdleTimeOut(moduleConfig.getRestIdleTimeOut())
                .jettyAcceptorPriorityDelta(moduleConfig.getRestAcceptorPriorityDelta())
                .jettyMinThreads(moduleConfig.getRestMinThreads())
                .jettyMaxThreads(moduleConfig.getRestMaxThreads())
                .jettyAcceptQueueSize(moduleConfig.getRestAcceptQueueSize())
                .jettyHttpMaxRequestHeaderSize(moduleConfig.getHttpMaxRequestHeaderSize())
                .build();


        jettyServer = new JettyServer(jettyServerConfig);
        jettyServer.initialize();

        this.registerServiceImplementation(JettyHandlerRegister.class, new JettyHandlerRegisterImpl(jettyServer));


        camelContext = new DefaultCamelContext();
        camelContext.addComponent(Const.CAMEL_ZABBIX_COMPONENT_NAME, new ZabbixSenderComponent(getManager()));

        this.registerServiceImplementation(CamelContextHolderService.class, new CamelContextHolderService(camelContext, getManager()));
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        try {

            jettyServer.start();
            camelContext.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {
        JettyHandlerRegister service = getManager().find(CoreModule.NAME).provider().getService(JettyHandlerRegister.class);
        service.addHandler(new HttpItemTrapperHandler());
    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                TelemetryModule.NAME,
                ZabbixSenderModule.NAME
        };
    }
}
