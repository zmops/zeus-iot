package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine.dao;

import com.zmops.zeus.iot.server.core.analysis.manual.history.History;
import com.zmops.zeus.iot.server.core.analysis.manual.history.StrHistory;
import com.zmops.zeus.iot.server.core.analysis.manual.history.TextHistory;
import com.zmops.zeus.iot.server.core.analysis.manual.history.UIntHistory;
import com.zmops.zeus.iot.server.core.storage.StorageData;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.SQLBuilder;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.SQLExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/9/5 0:29
 */

@Slf4j
public class TDEngineSqlExecutor {

    protected <T extends StorageData> SQLExecutor getInsertExecutor(String modelName, T metrics) {

        SQLBuilder sqlBuilder = new SQLBuilder().append("h_").append(metrics.itemid() + " USING ");

        if (modelName.equals("history")) {

            History history = (History) metrics;
            sqlBuilder.append("history (deviceid, itemid) TAGS")
                    .append("('").append(history.getDeviceId()).append("'")
                    .append(",").append(history.getItemid() + " )")
                    .append(" VALUES (")
                    .append(history.getClock() + "").append(",").append(history.getValue()).append(")");

        } else if (modelName.equals("history_uint")) {

            UIntHistory uihistory = (UIntHistory) metrics;
            sqlBuilder.append("history_uint (deviceid, itemid) TAGS")
                    .append(" ('").append(uihistory.getDeviceId()).append("'")
                    .append(",").append(uihistory.getItemid() + " )")
                    .append(" VALUES (")
                    .append(uihistory.getClock() + "").append(",").append(uihistory.getValue()).append(")");
        } else if (modelName.equals("history_text")) {

            TextHistory textHistory = (TextHistory) metrics;
            String value = textHistory.getValue().replaceAll(",", "\\,");

            sqlBuilder.append("history_text (deviceid, itemid) TAGS")
                    .append(" ('").append(textHistory.getDeviceId()).append("'")
                    .append(",").append(textHistory.getItemid() + " )")
                    .append(" VALUES (")
                    .append(textHistory.getClock() + "").append(",'").append(value).append("')");
        } else if (modelName.equals("history_str")) {

            StrHistory strHistory = (StrHistory) metrics;
            String value = strHistory.getValue().replaceAll(",", "\\,");
            sqlBuilder.append("history_str (deviceid, itemid) TAGS")
                    .append(" ('").append(strHistory.getDeviceId()).append("'")
                    .append(",").append(strHistory.getItemid() + " )")
                    .append(" VALUES (")
                    .append(strHistory.getClock() + "").append(",'").append(value).append("')");
        }

        List<Object> param = new ArrayList<>();
        return new SQLExecutor(sqlBuilder.toString(), param);
    }

}
