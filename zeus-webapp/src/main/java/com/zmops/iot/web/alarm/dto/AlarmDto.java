package com.zmops.iot.web.alarm.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class AlarmDto {

    private String eventid;

    private String source;

    private String object;

    private String clock;

    private String ns;

    private String r_eventid;

    private String r_clock;

    private String r_ns;

    private String correlationid;

    private String userid;

    private String name;

    private String acknowledged;

    private String severity;

    private String opdata;

    private String acknowledges;
}
