package com.zmops.iot.web.event.pgEvent.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class EventDataDto {
    private String eventid;

    private String objectid;

    private String name;

    private String tag;

    private String tagValue;

    private String recoveryValue;

    private Integer acknowledged;

    private Integer clock;

    private Integer rClock;

    private Integer severity;
}
