package com.zmops.zeus.iot.server.core.analysis.manual.history;

import com.zmops.zeus.iot.server.core.analysis.Stream;
import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.analysis.worker.RecordStreamProcessor;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/9/6 17:07
 */
@Getter
@Setter
@Stream(name = "uint_hitory", processor = RecordStreamProcessor.class)
public class UIntHistory extends Record {

    private Long clock;

    private Long ns;

    private String value; // 浮点数

    private Map<String, String> itemTags;

    private List<String> hostGroups;

    @Override
    public String id() {
        return null;
    }

    @Override
    public void setValue(String value, Long clock) {
        this.value = value;
        this.clock = clock;
    }
}
