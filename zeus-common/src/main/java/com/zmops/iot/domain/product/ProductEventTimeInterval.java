package com.zmops.iot.domain.product;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 * <p>
 * 触发器 时间 表达式 函数
 */

@Data
@Entity
@Table(name = "product_event_time_interval")
public class ProductEventTimeInterval {

    @Id
    private Long eventTimeId;

    private Long eventRuleId;

    private String startTime;

    private String endTime;

    private String  dayOfWeeks;
}
