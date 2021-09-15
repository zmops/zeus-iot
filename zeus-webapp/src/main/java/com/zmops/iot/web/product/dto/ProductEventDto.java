package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProductEventDto {

    private String eventRuleId;

    private Byte eventNotify;

    private String eventRuleName;

    private String eventLevel;

    @CachedValue(value = "STATUS")
    private String status;

    private String remark;


}
