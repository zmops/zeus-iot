package com.zmops.zeus.iot.server.h2.provider;

import com.zmops.zeus.iot.server.client.jdbc.JDBCClientException;
import com.zmops.zeus.iot.server.client.jdbc.hikaricp.JDBCHikariCPClient;
import com.zmops.zeus.iot.server.h2.service.InsertDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yefei
 **/
public class LocalH2InsertDAO implements InsertDAO {

    private final JDBCHikariCPClient h2Client;

    public LocalH2InsertDAO(JDBCHikariCPClient h2Client) {
        this.h2Client = h2Client;
    }

    @Override
    public void insert(String sql) {
        try {
            h2Client.execute(h2Client.getConnection(), sql);
        } catch (JDBCClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int update(String sql, Object... params) {
        int r = 0;
        try {
            r = h2Client.executeUpdate(h2Client.getConnection(), sql, params);
        } catch (JDBCClientException e) {
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public ResultSet queryRes(String sql, Object... params) {
        ResultSet resultSet = null;
        try {
            resultSet = h2Client.executeQuery(h2Client.getConnection(), sql, params);
        } catch (JDBCClientException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    @Override
    public void delete(String sql) {
        try {
            h2Client.execute(h2Client.getConnection(), sql);
        } catch (JDBCClientException e) {
            e.printStackTrace();
        }
    }

}
