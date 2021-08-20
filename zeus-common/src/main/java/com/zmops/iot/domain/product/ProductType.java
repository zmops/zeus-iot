package com.zmops.iot.domain.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "product_type")
public class ProductType {
    @Id
    private Long id;

    private Long pid = 0L;

    private String pids;

    private String name;

    private String remark;
}
