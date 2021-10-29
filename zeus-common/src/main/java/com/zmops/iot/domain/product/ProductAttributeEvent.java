package com.zmops.iot.domain.product;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "product_attribute_event")
public class ProductAttributeEvent extends BaseEntity {

    @Id
    private Long attrId; //属性ID

    @JsonProperty("attrName")
    private String name; // 属性名称

    private String key; // 属性唯一Key

    private String units; // 单位 0 3 才有

    private Byte eventLevel;

    private String remark; // 备注

    private String valueType;

    private String productId; // 产品ID

    private String zbxId;  // Zabbix itemId

    private Long templateId;//继承的ID

    private String source; // 来源

    private Integer delay;

    private String unit;

    private String valuemapid;

    private Long depAttrId;

}
