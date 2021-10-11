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
@Table(name = "product_service_param")
public class ProductServiceParam {

    @Id
    private Long   id;
    private Long   serviceId;
    private String name;
    private String key;
    private String remark;
}
