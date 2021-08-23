package com.zmops.iot.web.analyse.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class LatestDto {

    private String name;

    private String itemid;

    private Long attrId;

    private String clock;

    private String value;

    private String change;

    private String tags;

}
