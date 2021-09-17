package com.zmops.iot.web.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class AlarmDto {

    private String eventid;

    private String clock;

    private String rClock;

    private String name;

    @CachedValue(value = "EVENT_LEVEL")
    private String severity;

    private String deviceName;
}
