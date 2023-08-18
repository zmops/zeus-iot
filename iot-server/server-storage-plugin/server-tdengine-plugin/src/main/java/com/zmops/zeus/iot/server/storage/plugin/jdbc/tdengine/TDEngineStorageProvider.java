package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine;

import com.zmops.zeus.iot.server.client.jdbc.hikaricp.JDBCHikariCPClient;
import com.zmops.zeus.iot.server.core.CoreModule;
import com.zmops.zeus.iot.server.core.storage.IBatchDAO;
import com.zmops.zeus.iot.server.core.storage.StorageDAO;
import com.zmops.zeus.iot.server.core.storage.StorageException;
import com.zmops.zeus.iot.server.core.storage.StorageModule;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine.dao.TDEngineStorageDAO;
import com.zmops.zeus.iot.server.telemetry.TelemetryModule;
import com.zmops.zeus.iot.server.telemetry.api.HealthCheckMetrics;
import com.zmops.zeus.iot.server.telemetry.api.MetricsCreator;
import com.zmops.zeus.iot.server.telemetry.api.MetricsTag;
import com.zmops.zeus.server.library.module.*;

import java.util.Properties;

/**
 * @author nantian created at 2021/9/3 23:39
 */
public class TDEngineStorageProvider extends ModuleProvider {

    private final TDEngineStorageConfig config;
    private JDBCHikariCPClient client;

    public TDEngineStorageProvider() {
        this.config = new TDEngineStorageConfig();
    }

    @Override
    public String name() {
        return "tdengine";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return StorageModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return config;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {
        Properties settings = new Properties();
        settings.setProperty("jdbcUrl", config.getUrl());
        settings.setProperty("dataSource.user", config.getUser());
        settings.setProperty("dataSource.password", config.getPassword());
        client = new JDBCHikariCPClient(settings);

        this.registerServiceImplementation(IBatchDAO.class, new TDEngineBatchDAO(client));
        this.registerServiceImplementation(StorageDAO.class, new TDEngineStorageDAO(getManager(), client));
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {

        MetricsCreator metricCreator = getManager().find(TelemetryModule.NAME).provider().getService(MetricsCreator.class);
        HealthCheckMetrics healthChecker = metricCreator.createHealthCheckerGauge("storage_tdengine", MetricsTag.EMPTY_KEY, MetricsTag.EMPTY_VALUE);

        client.registerChecker(healthChecker);
        client.connect();

        TDEngineDatabaseInstaller installer = new TDEngineDatabaseInstaller(client, getManager(), config);
        try {
            installer.createDatabase();
        } catch (StorageException e) {
            e.printStackTrace();
        }
//            getManager().find(CoreModule.NAME).provider().getService(ModelCreator.class).addModelListener(installer);
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public String[] requiredModules() {
        return new String[]{CoreModule.NAME};
    }
}
