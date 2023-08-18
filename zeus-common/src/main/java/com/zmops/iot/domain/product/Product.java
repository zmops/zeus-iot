package com.zmops.iot.domain.product;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/8/3 20:08
 * <p>
 * 产品
 */

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    private Long productId;

    private Long groupId;

    private String name;

    private String type;

    private String manufacturer;

    private String model;

    private String remark;

    private String productCode;

    private String zbxId;

    private Long tenantId;

    private String icon;
    
}
