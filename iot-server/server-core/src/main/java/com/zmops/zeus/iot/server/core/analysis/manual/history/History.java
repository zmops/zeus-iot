package com.zmops.zeus.iot.server.core.analysis.manual.history;

import com.zmops.zeus.iot.server.core.analysis.Stream;
import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.analysis.worker.RecordStreamProcessor;
import com.zmops.zeus.iot.server.core.storage.StorageData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/9/6 17:07
 */
@Getter
@Setter
@Stream(name = "history", processor = RecordStreamProcessor.class)
public class History extends Record {

    private Long clock;

    private Long ns;

    private String value; // 浮点数

    private String deviceId; // host

    private Map<String, String> itemTags;

    private List<String> hostGroups;

    @Override
    public String id() {
        return null;
    }

    @Override
    public void setValue(String deviceId,String value, Long clock) {
        this.deviceId = deviceId;
        this.value = value;
        this.clock = clock;
    }
}
