package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine.dao;

import com.zmops.zeus.iot.server.core.analysis.manual.history.History;
import com.zmops.zeus.iot.server.core.analysis.manual.history.UIntHistory;
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

        SQLBuilder sqlBuilder = new SQLBuilder().append("h_").append(metrics.itemid() + " USING ");

        if (modelName.equals("history")) {

            History history = (History) metrics;
            sqlBuilder.append("history TAGS('").append(history.getDeviceId()).append("') VALUES (")
                    .append(history.getClock() + "").append(",").append(history.getValue()).append(")");

        } else if (modelName.equals("history_uint")) {

            UIntHistory uihistory = (UIntHistory) metrics;
            sqlBuilder.append("history_uint TAGS('").append(uihistory.getDeviceId()).append("') VALUES (")
                    .append(uihistory.getClock() + "").append(",").append(uihistory.getValue()).append(")");
        }

        List<Object> param = new ArrayList<>();
        return new SQLExecutor(sqlBuilder.toString(), param);
    }

}
