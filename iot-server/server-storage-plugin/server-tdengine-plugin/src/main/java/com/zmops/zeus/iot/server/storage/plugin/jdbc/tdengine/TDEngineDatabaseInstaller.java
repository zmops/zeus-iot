package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine;

import com.zmops.zeus.iot.server.core.storage.StorageException;
import com.zmops.zeus.iot.server.library.client.Client;
import com.zmops.zeus.iot.server.library.client.jdbc.JDBCClientException;
import com.zmops.zeus.iot.server.library.client.jdbc.hikaricp.JDBCHikariCPClient;
import com.zmops.zeus.iot.server.library.module.ModuleManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author nantian created at 2021/9/4 1:16
 */
public class TDEngineDatabaseInstaller {

    protected final Client                client;
    private final   ModuleManager         moduleManager;
    private final   TDEngineStorageConfig config;

    private static final String CREATE_ZEUS_STABLE_HISTORY      = "create stable if not exists history(clock TIMESTAMP, value DOUBLE) tags (deviceid BINARY(20))";
    private static final String CREATE_ZEUS_STABLE_HISTORY_UINT = "create stable if not exists history_uint(clock TIMESTAMP, value BIGINT) tags (deviceid BINARY(20))";

    private static final String CREATE_ZEUS_STABLE_TRENDS      = "create stable if not exists trends(clock TIMESTAMP, value_min DOUBLE, value_avg DOUBLE, value_max DOUBLE) tags (hostid BINARY(20))";
    private static final String CREATE_ZEUS_STABLE_TRENDS_UINT = "create stable if not exists trends_uint(clock TIMESTAMP, value_min BIGINT, value_avg BIGINT, value_max BIGINT) tags (hostid BINARY(20))";

    public TDEngineDatabaseInstaller(Client client, ModuleManager moduleManager, TDEngineStorageConfig config) {
        this.config = config;
        this.client = client;
        this.moduleManager = moduleManager;
    }

    protected void createDatabase() throws StorageException {
        JDBCHikariCPClient jdbcHikariCPClient = (JDBCHikariCPClient) client;
        try (Connection connection = jdbcHikariCPClient.getConnection()) {

            jdbcHikariCPClient.execute(connection, CREATE_ZEUS_STABLE_HISTORY);
            jdbcHikariCPClient.execute(connection, CREATE_ZEUS_STABLE_HISTORY_UINT);
            jdbcHikariCPClient.execute(connection, CREATE_ZEUS_STABLE_TRENDS);
            jdbcHikariCPClient.execute(connection, CREATE_ZEUS_STABLE_TRENDS_UINT);

        } catch (JDBCClientException | SQLException e) {
            throw new StorageException(e.getMessage(), e);
        }

    }

}
