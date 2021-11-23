package com.zmops.zeus.iot.server.storage.plugin.none;

import com.zmops.zeus.iot.server.core.storage.IBatchDAO;
import com.zmops.zeus.iot.server.core.storage.StorageDAO;
import com.zmops.zeus.iot.server.core.storage.StorageModule;
import com.zmops.zeus.server.library.module.*;

/**
 * @author nantian created at 2021/9/27 21:18
 * <p>
 * Proxy 模式下，或者 不使用默认的 TDEngine 时
 */
public class NoneStorageProvider extends ModuleProvider {
    @Override
    public String name() {
        return "none";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return StorageModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return new ModuleConfig() {
        };
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {

        this.registerServiceImplementation(IBatchDAO.class, new BatchDaoNoop());
        this.registerServiceImplementation(StorageDAO.class, new StorageDAONoop());

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
