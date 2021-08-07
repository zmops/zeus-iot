package com.zmops.iot.web.product.dto;

import lombok.Data;

/**
 * @author nantian created at 2021/8/5 16:06
 * <p>
 * 设备 离线/上线 规则
 */

@Data
public class ProductOfflineRule {

    private String productAttrId; // 设备属性ID

    private String ruleType; // 触发离线 还是 触发在线

    private String ruleFunction; // nodata 或者 last

    private String ruleValue;  // 时间 或者 值

}
