package com.zmops.iot.web.device.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yefei
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = CachedValueFilter.class)
public class DeviceLogDto {

    private String logType;

    private String triggerTime;

    private String content;

    @CachedValue(type = DicType.Device)
    private String deviceId;

    @CachedValue(value = "EVENT_LEVEL")
    private String severity;

    private String param;

    private String status;

    private String triggerType;

    private String triggerBody;

    private String key;

    private Long eventRuleId;

    private Long userId;

    private List<DeviceRelationDto> triggerDevice;

    private List<DeviceRelationDto> executeDevice;

}
