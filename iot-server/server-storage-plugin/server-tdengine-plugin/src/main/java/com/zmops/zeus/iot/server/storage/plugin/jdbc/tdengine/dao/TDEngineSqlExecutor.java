package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine.dao;

import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.storage.StorageData;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.SQLBuilder;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.SQLExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/9/5 0:29
 */

@Slf4j
public class TDEngineSqlExecutor {


    protected <T extends StorageData> SQLExecutor getInsertExecutor(String modelName, T metrics) {

        SQLBuilder sqlBuilder = new SQLBuilder("INSERT INTO " + modelName + " VALUES");

        List<Object> param = new ArrayList<>();

        return new SQLExecutor(sqlBuilder.toString(), param);
    }

}
