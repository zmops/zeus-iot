package com.zmops.iot.web.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class AlarmDto {

    private Long eventId;

    private Long objectId;

    private LocalDateTime clock;

    private LocalDateTime rClock;

    private String name;

    private String acknowledged;

    @CachedValue(type = DicType.Device)
    private String deviceId;

    @CachedValue(value = "EVENT_LEVEL")
    private String severity;

    private String status;

}
