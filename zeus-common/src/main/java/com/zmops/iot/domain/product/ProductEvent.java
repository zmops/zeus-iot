package com.zmops.iot.domain.product;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/9/15 1:02
 */

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "product_event")
public class ProductEvent extends BaseEntity {

    @Id
    private Long eventRuleId;
    private String eventRuleName;
    private String eventLevel;
    private String expLogic;
    private String classify;
    private String eventNotify;
    private Long   tenantId;
    private String  status;
    private String  remark;
    private Integer taskId;
    private Integer triggerType;

}
