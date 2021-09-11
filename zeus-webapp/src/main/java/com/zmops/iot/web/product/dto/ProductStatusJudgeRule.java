package com.zmops.iot.web.product.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author nantian created at 2021/8/5 16:06
 * <p>
 * 设备 离线/上线 规则  last(/Zabbix server/system.cpu.util[,user])>10
 */

@Data
public class ProductStatusJudgeRule {


    @NotNull
    private String deviceId; // deviceid

    private String ruleId; // 自动生成，trigger name

    private String triggerId; // zbxId


    //#####################  下线规则

    @NotNull
    private Long productAttrId; // 设备属性ID

    @NotNull
    private String ruleType; // 触发离线 还是 触发在线

    @NotNull
    private String ruleFunction; // nodata 或者 > < = 函数

    @NotNull
    private String ruleCondition;  // 时间 或者 特定值

    @NotNull
    private String productAttrKey; // 属性 Key


    //#####################  上线规则

    @NotNull
    private Long productAttrIdRecovery; // 设备属性ID

    @NotNull
    private String ruleFunctionRecovery; // nodata 或者 > < = 函数

    @NotNull
    private String ruleConditionRecovery;  // 时间 或者 特定值

    @NotNull
    private String productAttrKeyRecovery; // 属性 Key
}
