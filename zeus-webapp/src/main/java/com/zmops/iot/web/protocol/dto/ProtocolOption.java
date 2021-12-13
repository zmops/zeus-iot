package com.zmops.iot.web.protocol.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author yefei
 **/
@Data
public class ProtocolOption {
    private String routeId;

    private String protocol;

//    private ProtocolAction action;

    private Map<String, Object> options;
}
