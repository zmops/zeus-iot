package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProductEventDto {

    private Long eventRuleId;

    private Byte eventNotify;

    private String eventRuleName;

    @CachedValue(value = "EVENT_LEVEL", fieldName = "eventLevelName")
    private String eventLevel;

    @CachedValue(value = "STATUS", fieldName = "statusName")
    private String status;

    @CachedValue(value = "WHETHER", fieldName = "inheritName")
    private String inherit;

    private String remark;
    private String classify;
    private String expLogic;
    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    private Long createUser;
    private String createTime;
    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    private Long updateUser;
    private String updateTime;

    @CachedValue(type= DicType.Tenant, fieldName = "tenantName")
    private Long tenantId;

    private Integer taskId;

    @CachedValue(value = "triggerType", fieldName = "triggerTypeName")

    private String triggerType;

}
