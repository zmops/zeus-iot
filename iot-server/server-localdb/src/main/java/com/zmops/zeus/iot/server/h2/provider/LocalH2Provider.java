package com.zmops.zeus.iot.server.h2.provider;

import com.zmops.zeus.iot.server.h2.module.LocalH2Module;
import com.zmops.zeus.iot.server.library.client.jdbc.JDBCClientException;
import com.zmops.zeus.iot.server.library.client.jdbc.hikaricp.JDBCHikariCPClient;
import com.zmops.zeus.iot.server.library.module.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author nantian created at 2021/10/24 16:57
 */
public class LocalH2Provider extends ModuleProvider {

    private final LocalH2Config      localH2Config;
    private       JDBCHikariCPClient h2Client;

    public LocalH2Provider() {
        this.localH2Config = new LocalH2Config();
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return LocalH2Module.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return localH2Config;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {

        Properties settings = new Properties();
        settings.setProperty("dataSourceClassName", localH2Config.getDriver());
        settings.setProperty("dataSource.url", localH2Config.getUrl());
        settings.setProperty("dataSource.user", localH2Config.getUser());
        settings.setProperty("dataSource.password", localH2Config.getPassword());
        h2Client = new JDBCHikariCPClient(settings);

        h2Client.connect();

        try (Connection connection = h2Client.getConnection();
             ResultSet rs = h2Client.executeQuery(
                     connection, "CREATE TABLE IF NOT EXISTS `sys_user` (`user_id` bigint NOT NULL COMMENT '主键id',PRIMARY KEY (`user_id`) USING BTREE );")) {

            h2Client.execute(connection, "insert into `sys_user` VALUES (1);");

            ResultSet rs2 = h2Client.executeQuery(connection, "select * from sys_user;");

            System.out.println(123);

        } catch (SQLException | JDBCClientException e) {
            // throw new IOException(e.getMessage(), e);
        }


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
