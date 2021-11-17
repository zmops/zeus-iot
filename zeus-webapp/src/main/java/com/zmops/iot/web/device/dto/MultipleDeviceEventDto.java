package com.zmops.iot.web.device.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.domain.product.ProductEventExpression;
import com.zmops.iot.domain.product.ProductEventService;
import com.zmops.iot.domain.product.ProductEventTimeInterval;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import com.zmops.iot.web.product.dto.ProductEventRuleDto;
import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class MultipleDeviceEventDto {

    private Long eventRuleId;

    private Byte eventNotify;

    private String eventRuleName;

    @CachedValue(value = "EVENT_LEVEL")
    private String eventLevel;

    @CachedValue(value = "STATUS")
    private String status;

    private String inherit;

    private String remark;
    private String classify;
    private String expLogic;
    @CachedValue(type = DicType.SysUserName)
    private Long createUser;
    private String createTime;
    @CachedValue(type = DicType.SysUserName)
    private Long updateUser;
    private String updateTime;

    private String triggerDevice;

    private String executeDevice;

    private Integer taskId;

    @CachedValue(value = "triggerType")
    private String triggerType;

    private String scheduleConf;

    private List<ProductEventExpression> expList;

    private List<ProductEventService> deviceServices;

    private List<MultipleDeviceEventRule.Tag> tags;

    private List<ProductEventTimeInterval> timeExpList;
}
