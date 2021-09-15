package com.zmops.iot.domain.product;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/8/11 17:02
 */

@Getter
@Setter
@Entity
@Table(name = "product_service_relation")
public class ProductServiceRelation {

    @Id
    private Long id;

    private Long serviceId;

    private String relationId;

    private String inherit;

}
