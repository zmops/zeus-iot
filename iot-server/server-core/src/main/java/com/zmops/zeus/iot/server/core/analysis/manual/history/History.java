package com.zmops.zeus.iot.server.core.analysis.manual.history;

import com.zmops.zeus.iot.server.core.analysis.Stream;
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
@Stream(name = "hitory", processor = RecordStreamProcessor.class)
public class History implements StorageData {

    private Integer itemid;

    private Long clock;

    private Long ns;

    private Long value; // 浮点数

    private Map<String, String> itemTags;

    private List<String> hostGroups;

    @Override
    public String id() {
        return null;
    }


}
