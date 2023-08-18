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
@Table(name = "product_status_function_relation")
public class ProductStatusFunctionRelation {

    @Id
    private Long id;

    private Long ruleId;

    private String relationId;

    private String inherit;

    // 离线触发器ID
    private String zbxId;

    // 上线触发器ID
    private String zbxIdRecovery;

}
