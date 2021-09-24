package com.zmops.zeus.iot.server.transfer.provider;

import com.zmops.zeus.iot.server.core.CoreModule;
import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.transfer.core.TransferManager;
import com.zmops.zeus.iot.server.transfer.module.ServerTransferModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nantian created at 2021/9/22 16:44
 */
public class ServerTransferProvider extends ModuleProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerTransferProvider.class);

    private final ServerTransferConfig config;

    public ServerTransferProvider() {
        this.config = new ServerTransferConfig();
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
        return config;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {

    }


    /**
     * Stopping agent gracefully if get killed.
     *
     * @param manager - agent manager
     */
    private static void stopManagerIfKilled(TransferManager manager) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOGGER.info("stopping agent gracefully");
                manager.stop();
            } catch (Exception ex) {
                LOGGER.error("exception while stopping threads", ex);
            }
        }));
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        TransferManager manager = new TransferManager(config);
        try {
            manager.start();
            stopManagerIfKilled(manager);
//            manager.join();
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
                CoreModule.NAME
        };
    }
}
