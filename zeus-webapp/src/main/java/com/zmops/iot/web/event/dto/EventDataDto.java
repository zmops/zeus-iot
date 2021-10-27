package com.zmops.iot.web.event.dto;

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
}
