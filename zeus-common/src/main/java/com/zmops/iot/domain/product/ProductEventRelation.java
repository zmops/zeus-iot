package com.zmops.iot.domain.product;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/8/11 17:02
 */

@Getter
@Setter
@Entity
@Table(name = "product_event_relation")
public class ProductEventRelation {

    @Id
    private Long id;

    private Long eventRuleId;

    private String relationId;

    private Integer zbxId;

    private String inherit;

}
