package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine.dao;

import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.storage.IRecordDAO;
import com.zmops.zeus.iot.server.core.storage.model.Model;
import com.zmops.zeus.iot.server.client.jdbc.hikaricp.JDBCHikariCPClient;
import com.zmops.zeus.iot.server.client.request.InsertRequest;

import java.io.IOException;

/**
 * @author nantian created at 2021/9/5 0:27
 */
public class TDEngineRecordDAO extends TDEngineSqlExecutor implements IRecordDAO {

    private final JDBCHikariCPClient jdbcClient;

    public TDEngineRecordDAO(JDBCHikariCPClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public InsertRequest prepareBatchInsert(Model model, Record record) throws IOException {
        return getInsertExecutor(model.getName(), record);
    }
}
