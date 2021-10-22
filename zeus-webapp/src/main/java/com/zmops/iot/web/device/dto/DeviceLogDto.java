package com.zmops.iot.web.device.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime triggerTime;
    private String content;
    private String deviceId;
    private String deviceName;
    @CachedValue(value = "EVENT_LEVEL")
    private String severity;
    private String param;
    private String status;
}
