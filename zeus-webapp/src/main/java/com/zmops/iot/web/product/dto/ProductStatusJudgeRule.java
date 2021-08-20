package com.zmops.iot.web.product.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author nantian created at 2021/8/5 16:06
 * <p>
 * 设备 离线/上线 规则
 */

@Data
public class ProductStatusJudgeRule {

    @NotNull
    private Long productAttrId; // 设备属性ID

    @NotNull
    private String ruleType; // 触发离线 还是 触发在线

    @NotNull
    private String ruleFunction; // nodata 或者 last 函数

    @NotNull
    private String ruleValue;  // 时间 或者 值


    private String triggerName; // ID
    private String itemKey;
    private String hostName;


    public static final String ON_LINE  = "online"; // 上线触发器
    public static final String OFF_LINE = "offline"; // 离线触发器
    public static final String NODATA   = "nodata";
    public static final String LASTDATA = "lastdata";

}
