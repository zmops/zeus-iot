package com.zmops.zeus.driver.entity;

import lombok.Data;

/**
 * @author nantian created at 2021/8/10 17:52
 */
@Data
public class ZbxProblemInfo {

    private String eventid;

    private String source;

    private String object;

    private String objectid;

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
