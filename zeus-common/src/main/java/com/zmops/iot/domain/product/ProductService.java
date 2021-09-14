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
@Table(name = "product_service")
public class ProductService {
    @Id
    private Long id;

    private String name;
    private String mark;
    private String remark;
    private Long   templateId;
    private String async;
}
