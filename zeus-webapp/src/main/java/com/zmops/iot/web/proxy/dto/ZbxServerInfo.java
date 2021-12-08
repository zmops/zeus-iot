package com.zmops.iot.web.proxy.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class ZbxServerInfo {
    private String count;
    private Attributes attributes;

    @Data
    public static class Attributes{
        private String proxyid;
        private int status;
        private int state;
    }
}
