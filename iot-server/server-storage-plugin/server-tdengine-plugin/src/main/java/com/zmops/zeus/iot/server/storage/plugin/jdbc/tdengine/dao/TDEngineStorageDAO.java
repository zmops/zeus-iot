package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine.dao;

import com.zmops.zeus.iot.server.core.storage.IRecordDAO;
import com.zmops.zeus.iot.server.core.storage.StorageDAO;
import com.zmops.zeus.iot.server.library.client.jdbc.hikaricp.JDBCHikariCPClient;
import com.zmops.zeus.iot.server.library.module.ModuleManager;
import lombok.RequiredArgsConstructor;

/**
 * @author nantian created at 2021/9/6 16:33
 */
@RequiredArgsConstructor
public class TDEngineStorageDAO implements StorageDAO {

    private final ModuleManager      manager;
    private final JDBCHikariCPClient client;


    @Override
    public IRecordDAO newRecordDao() {
        return new TDEngineRecordDAO(client);
    }
}
