package com.zmops.iot.domain.product;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/8/5 13:03
 */

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "product_attribute")
public class ProductAttribute extends BaseEntity {

    @Id
    private Long attrId; //属性ID

    private String name; // 属性名称

    private String key; // 属性唯一Key

    private String uints; // 单位 0 3 才有

    private String source; // 来源

    private String remark; // 备注

    private Long productId; // 产品ID

    private Integer zbxId;  // Zabbix itemId
}
