package com.zmops.iot.web.product.dto;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author nantian created at 2021/8/5 16:06
 * <p>
 * 设备 离线/上线 规则  last(/Zabbix server/system.cpu.util[,user])>10
 */

@Data
public class ProductStatusJudgeRule {


    @NotBlank(groups = {BaseEntity.Create.class})
    private String relationId; // deviceid

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Delete.class})
    private Long ruleId; // 自动生成，trigger name

    private String ruleName;

    private String triggerId; // zbxId

    //#####################  下线规则

    @NotNull(groups = {BaseEntity.Create.class})
    private Long attrId; // 设备属性ID

    @NotBlank(groups = {BaseEntity.Create.class})
    private String ruleFunction; // nodata 或者 > < = 函数

    @NotBlank(groups = {BaseEntity.Create.class})
    private String ruleCondition;  // 时间 或者 特定值

    @NotNull(groups = {BaseEntity.Create.class})
    private String unit; //单位 s m h 或空

    @NotBlank(groups = {BaseEntity.Create.class})
    private String productAttrKey; // 属性 Key


    //#####################  上线规则

    @NotNull(groups = {BaseEntity.Create.class})
    private Long attrIdRecovery; // 设备属性ID

    @NotBlank(groups = {BaseEntity.Create.class})
    private String ruleFunctionRecovery; // nodata 或者 > < = 函数

    @NotBlank(groups = {BaseEntity.Create.class})
    private String ruleConditionRecovery;  // 时间 或者 特定值

    @NotBlank(groups = {BaseEntity.Create.class})
    private String productAttrKeyRecovery; // 属性 Key

    private String unitRecovery;
}
