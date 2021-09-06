package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.zmops.zeus.iot.server.core.analysis.manual.history.History;
import com.zmops.zeus.iot.server.core.analysis.manual.history.UIntHistory;
import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.analysis.worker.RecordStreamProcessor;
import com.zmops.zeus.iot.server.library.server.jetty.ArgumentsParseException;
import com.zmops.zeus.iot.server.library.server.jetty.JettyJsonHandler;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author nantian created at 2021/9/2 12:58
 */
public class DataTransferHandler extends JettyJsonHandler {

    private final Gson gson = new Gson();

    @Override
    public String pathSpec() {
        return "/zabbix/data-transfer";
    }

    /**
     * 接收来自 zeus_transfer 数据, 写入时序库
     *
     * @param req request
     * @return NULL
     * @throws ArgumentsParseException
     * @throws IOException
     */
    @Override
    protected JsonElement doPost(HttpServletRequest req) throws ArgumentsParseException, IOException {
        GZIPInputStream gzin   = new GZIPInputStream(req.getInputStream());
        BufferedReader  reader = new BufferedReader(new InputStreamReader(gzin));

        String line;
        while ((line = reader.readLine()) != null) {
            insert(line);
        }

        return null;
    }

    private void insert(String itemRecord) {
        ItemValue itemValue = gson.fromJson(itemRecord, ItemValue.class);

        Record record;

        if (itemValue.getType() == 3) { //uint
            record = new UIntHistory();
        } else {
            record = new History();
        }

        record.setItemid(itemValue.itemid);
        record.setValue(itemValue.getHost().get("host"), itemValue.value, itemValue.clock);

        RecordStreamProcessor.getInstance().in(record);
    }


    @Setter
    @Getter
    static class ItemValue {

        private Integer type;

        private String name;

        private Long clock;

        private Integer itemid;

        private Long ns;

        private String value;

        private Map<String, String> host;
    }
}
