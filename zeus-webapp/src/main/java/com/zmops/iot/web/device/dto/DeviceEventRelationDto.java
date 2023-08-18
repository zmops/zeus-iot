package com.zmops.iot.web.device.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class DeviceEventRelationDto {
    private Long id;
    private Long eventRuleId;
    private String relationId;
    private String zbxId;
    private String inherit;
    private String status;
    private String remark;
    private String triggerDevice;
}
