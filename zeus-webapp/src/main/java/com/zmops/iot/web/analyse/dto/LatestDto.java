package com.zmops.iot.web.analyse.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class LatestDto {

    private String name;

    private String itemid;

    private Long attrId;

    private String key;

    private String clock;

    private String value;

    private String originalValue;

    private String change;

    private String tags;

    @CachedValue(value = "UNITS")
    private String units;

}
