package com.zmops.iot.domain.schedule;

import com.zmops.iot.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/11/13 11:23
 */

@Getter
@Setter
@Table(name = "task_info")
@Entity
public class Task extends BaseEntity {


    @Id
    @Column(name = "id", nullable = false)
    private Integer id;


    private String scheduleType;            // 调度类型
    private String scheduleConf;            // 调度配置，值含义取决于调度类型
    private String misfireStrategy;         // 调度过期策略

    private Integer taskTimeout;

    private Integer taskFailRetryCount;

    private String triggerStatus;   // 调度状态：DISABLE-停止，ENABLE-运行
    private Long triggerLastTime;    // 上次调度时间
    private Long triggerNextTime;

    private String remark;

    private String executorParam;
}
