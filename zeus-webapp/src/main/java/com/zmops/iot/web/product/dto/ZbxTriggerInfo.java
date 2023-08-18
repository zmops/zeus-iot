package com.zmops.iot.web.product.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ZbxTriggerInfo {
    private List<Host> hosts;
    private List<Tag> tags;
    private String triggerid;
    private String description;


    @Data
    public static class Host{
        private String hostid;
        private String host;
    }

    @Data
    public static class Tag{
        private String tag;
        private String value;
    }
}
