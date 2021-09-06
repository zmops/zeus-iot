package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine;

import com.zmops.zeus.iot.server.library.client.Client;
import com.zmops.zeus.iot.server.library.module.ModuleManager;

/**
 * @author nantian created at 2021/9/4 1:16
 */
public class TDEngineDatabaseInstaller {

    protected final Client                client;
    private final   ModuleManager         moduleManager;
    private final   TDEngineStorageConfig config;

    public TDEngineDatabaseInstaller(Client client,
                                     ModuleManager moduleManager,
                                     TDEngineStorageConfig config) {
        this.config = config;
        this.client = client;
        this.moduleManager = moduleManager;
    }


    protected void createDatabase() {

    }

}
