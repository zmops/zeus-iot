package com.zmops.iot.web.proxy.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class ProxyMonitorInfo {
    private int hostStatusEnable;
    private int hostStatusDisable;
    private int hostStatusTotal;

    private int itemStatusEnable;
    private int itemStatusDisable;
    private int itemStatusUnSupport;
    private int itemStatusTotal;

    private int triggerStatusEnable;
    private int triggerStatusDisable;
    private int triggerStatusTotal;

    private String nvps;

    private String proxyId;
    private String proxyName;

    public int getHostStatusTotal() {
        return this.hostStatusEnable + this.hostStatusDisable;
    }

    public int getItemStatusTotal() {
        return this.itemStatusEnable + this.itemStatusDisable + itemStatusUnSupport;
    }

    public int getTriggerStatusTotal() {
        return this.triggerStatusEnable + this.triggerStatusDisable;
    }


}
