package com.zmops.zeus.driver.entity;

import lombok.Data;

import java.util.List;

/**
 * @author nantian created at 2021/8/10 17:52
 */
@Data
public class ZbxItemInfo {

    private String itemid;

    private String hostid;

    private String name;

    private String key_;

    private String status;

    private String value_type;

    private String units;

    private String valuemapid;

    private String interfaceid;

    private List<HostInfo> hosts;

    @Data
    public static class HostInfo {
        private String hostid;
        private String host;
    }
}
