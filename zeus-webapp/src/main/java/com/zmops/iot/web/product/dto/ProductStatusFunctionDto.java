package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

/**
 * @author nantian created at 2021/8/5 16:06
 * <p>
 * 设备 离线/上线 规则  last(/Zabbix server/system.cpu.util[,user])>10
 */

@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProductStatusFunctionDto {


    private String relationId;

    private Long ruleId;

    private Long attrId;

    private Integer ruleStatus;

    private String ruleFunction;

    private String ruleCondition;

    @CachedValue(value = "UNITS")
    private String unit;

    @CachedValue(value = "UNITS")
    private String units;

    @CachedValue(value = "UNITS")
    private String unitsRecovery;

    private String productAttrKey;

    private Long attrIdRecovery;

    private String ruleFunctionRecovery;

    private String ruleConditionRecovery;

    private String productAttrKeyRecovery;

    @CachedValue(value = "UNITS")
    private String unitRecovery;

    private String attrName;

    private String attrNameRecovery;

    @CachedValue(type = DicType.SysUserName)
    private Long createUser;
    private String createTime;
    @CachedValue(type = DicType.SysUserName)
    private Long updateUser;
    private String updateTime;
}
