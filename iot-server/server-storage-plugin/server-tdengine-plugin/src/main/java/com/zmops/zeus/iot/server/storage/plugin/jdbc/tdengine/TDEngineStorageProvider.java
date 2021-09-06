package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine;

import com.zmops.zeus.iot.server.core.CoreModule;
import com.zmops.zeus.iot.server.core.storage.IBatchDAO;
import com.zmops.zeus.iot.server.core.storage.StorageModule;
import com.zmops.zeus.iot.server.library.client.jdbc.hikaricp.JDBCHikariCPClient;
import com.zmops.zeus.iot.server.library.module.*;
import com.zmops.zeus.iot.server.telemetry.TelemetryModule;
import com.zmops.zeus.iot.server.telemetry.api.HealthCheckMetrics;
import com.zmops.zeus.iot.server.telemetry.api.MetricsCreator;
import com.zmops.zeus.iot.server.telemetry.api.MetricsTag;

import java.util.Properties;

/**
 * @author nantian created at 2021/9/3 23:39
 */
public class TDEngineStorageProvider extends ModuleProvider {

    private TDEngineStorageConfig config;
    private JDBCHikariCPClient    client;

    public TDEngineStorageProvider(TDEngineStorageConfig config) {
        this.config = config;
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
        return null;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {

        Properties settings = new Properties();
        settings.setProperty("dataSourceClassName", config.getDriver());
        settings.setProperty("dataSource.url", config.getUrl());
        settings.setProperty("dataSource.user", config.getUser());
        settings.setProperty("dataSource.password", config.getPassword());
        client = new JDBCHikariCPClient(settings);

        this.registerServiceImplementation(IBatchDAO.class, new TDEngineBatchDAO(client));
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {

        MetricsCreator metricCreator = getManager().find(TelemetryModule.NAME).provider().getService(MetricsCreator.class);
        HealthCheckMetrics healthChecker = metricCreator.createHealthCheckerGauge("storage_tdengine", MetricsTag.EMPTY_KEY, MetricsTag.EMPTY_VALUE);

        client.registerChecker(healthChecker);
        client.connect();

        TDEngineDatabaseInstaller installer = new TDEngineDatabaseInstaller(client, getManager(), config);
        installer.createDatabase();
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
