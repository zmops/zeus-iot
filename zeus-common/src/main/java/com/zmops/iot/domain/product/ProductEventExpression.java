package com.zmops.iot.domain.product;

import com.zmops.iot.constant.IdTypeConsts;
import io.ebean.Model;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/9/15 16:50
 * <p>
 * 触发器 表达式 函数
 */

@Data
@Entity
@Table(name = "product_event_expression")
public class ProductEventExpression extends Model {

    @Id
    @GeneratedValue(generator = IdTypeConsts.ID_SNOW)
    private Long eventExpId;

    private Long eventRuleId;
    private String function;
    private String scope;
    private String condition;
    private String value;
    private String deviceId;
    private String productAttrKey;
    private String unit;
    private Long productAttrId;
    private String productAttrType;
    private String period;
    private String attrValueType;
}
