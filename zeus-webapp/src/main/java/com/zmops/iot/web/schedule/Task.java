package com.zmops.iot.web.schedule;

import lombok.Data;

/**
 * @author nantian created at 2021/11/12 19:11
 */

@Data
public class Task {

    private Integer id;

    private String scheduleType;            // 调度类型
    private String scheduleConf;            // 调度配置，值含义取决于调度类型
    private String misfireStrategy;         // 调度过期策略

    private Integer taskTimeout;

    private Integer taskFailRetryCount;

    private Integer triggerStatus;   // 调度状态：0-停止，1-运行
    private Long triggerLastTime;    // 上次调度时间
    private Long triggerNextTime;

    private String remark;

}
