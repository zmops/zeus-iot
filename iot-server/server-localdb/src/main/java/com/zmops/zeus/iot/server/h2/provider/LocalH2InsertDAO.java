package com.zmops.zeus.iot.server.h2.provider;

import com.zmops.zeus.iot.server.h2.dao.InsertDAO;
import com.zmops.zeus.iot.server.library.client.jdbc.JDBCClientException;
import com.zmops.zeus.iot.server.library.client.jdbc.hikaricp.JDBCHikariCPClient;

import java.sql.ResultSet;

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
    public ResultSet queryRes(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = h2Client.executeQuery(h2Client.getConnection(), sql);
        } catch (JDBCClientException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

}
