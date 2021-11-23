package com.zmops.zeus.iot.server.core;

import com.zmops.zeus.iot.server.core.analysis.StreamAnnotationListener;
import com.zmops.zeus.iot.server.core.annotation.AnnotationScan;
import com.zmops.zeus.iot.server.core.storage.StorageException;
import com.zmops.zeus.iot.server.receiver.module.CamelReceiverModule;
import com.zmops.zeus.iot.server.telemetry.TelemetryModule;
import com.zmops.zeus.server.jetty.JettyServer;
import com.zmops.zeus.server.jetty.JettyServerConfig;
import com.zmops.zeus.server.library.module.*;

import java.io.IOException;

/**
 * @author nantian created at 2021/8/16 22:26
 */
public class CoreModuleProvider extends ModuleProvider {

    private final CoreModuleConfig moduleConfig;

    private JettyServer jettyServer;

    private final AnnotationScan annotationScan;


    public CoreModuleProvider() {
        super();
        this.moduleConfig = new CoreModuleConfig();
        this.annotationScan = new AnnotationScan();
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

        annotationScan.registerListener(new StreamAnnotationListener(getManager()));

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

        jettyServer.setFilterInitParameter("configClass", "com.zmops.zeus.iot.web.config.IoTConfig");

    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        try {
            annotationScan.scan();
        } catch (IOException | StorageException e) {
            throw new ModuleStartException(e.getMessage(), e);
        }
    }

    public void shutdown() {

    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {

        try {
            jettyServer.start();
        } catch (Exception e) {
            throw new ModuleStartException(e.getMessage(), e);
        }

    }

    @Override
    public String[] requiredModules() {
        return new String[]{
                TelemetryModule.NAME,
                CamelReceiverModule.NAME
        };
    }
}
