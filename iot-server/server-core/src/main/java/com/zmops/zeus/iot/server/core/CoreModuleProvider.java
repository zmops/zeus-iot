package com.zmops.zeus.iot.server.core;

import com.zmops.zeus.iot.server.action.ActionRouteIdentifier;
import com.zmops.zeus.iot.server.action.HelloWorldAction;
import com.zmops.zeus.iot.server.core.camel.CamelContextHolderService;
import com.zmops.zeus.iot.server.core.camel.ZabbixSenderComponent;
import com.zmops.zeus.iot.server.core.eventbus.EventBusService;
import com.zmops.zeus.iot.server.core.server.JettyHandlerRegister;
import com.zmops.zeus.iot.server.core.server.JettyHandlerRegisterImpl;
import com.zmops.zeus.iot.server.core.servlet.DeviceTriggerActionHandler;
import com.zmops.zeus.iot.server.core.servlet.HttpItemTrapperHandler;
import com.zmops.zeus.iot.server.core.servlet.ZabbixConfigHandler;
import com.zmops.zeus.iot.server.eventbus.core.EventControllerFactory;
import com.zmops.zeus.iot.server.eventbus.thread.ThreadPoolFactory;
import com.zmops.zeus.iot.server.eventbus.thread.entity.ThreadCustomization;
import com.zmops.zeus.iot.server.eventbus.thread.entity.ThreadParameter;
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

    private JettyServer            jettyServer;
    private ThreadPoolFactory      threadPoolFactory;
    private EventControllerFactory eventControllerFactory;


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

        threadPoolFactory = new ThreadPoolFactory(new ThreadCustomization(), new ThreadParameter());
        eventControllerFactory = new EventControllerFactory(threadPoolFactory);

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

        camelContext = new DefaultCamelContext();
        camelContext.addComponent(Const.CAMEL_ZABBIX_COMPONENT_NAME, new ZabbixSenderComponent(getManager()));

        this.registerServiceImplementation(JettyHandlerRegister.class, new JettyHandlerRegisterImpl(jettyServer));
        this.registerServiceImplementation(CamelContextHolderService.class, new CamelContextHolderService(camelContext, getManager()));
        this.registerServiceImplementation(EventBusService.class, new EventBusService(eventControllerFactory));
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

    public void shutdown() {
        threadPoolFactory.shutdown();
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {
        JettyHandlerRegister service = getManager().find(CoreModule.NAME).provider().getService(JettyHandlerRegister.class);
        service.addHandler(new HttpItemTrapperHandler());
        service.addHandler(new DeviceTriggerActionHandler(getManager()));
        service.addHandler(new ZabbixConfigHandler(moduleConfig.getZabbixConfigPath()));


        // ### 可以自定义添加 Action 的动作异步处理，指定 ID
        eventControllerFactory.getAsyncController(ActionRouteIdentifier.helloworld).register(new HelloWorldAction());

    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                TelemetryModule.NAME,
                ZabbixSenderModule.NAME
        };
    }
}
