package com.zmops.iot.domain.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "product_event_service")
public class ProductEventService {

    private Long   eventRuleId;
    private Long   serviceId;
    private String deviceId;
    private String executeDeviceId;
}
