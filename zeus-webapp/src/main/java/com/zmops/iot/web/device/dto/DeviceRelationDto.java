package com.zmops.iot.web.device.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class DeviceRelationDto {

    private String deviceId;

    private String name;

    private Long eventRuleId;

}
