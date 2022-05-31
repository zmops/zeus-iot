package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.product.dto.ProductEventRule;

/**
 * @author yefei
 **/
public class ProductEventTriggerCreateEvent extends BaseEvent<ProductEventRule> {
    public ProductEventTriggerCreateEvent(Object source, ProductEventRule eventData) {
        super(source, eventData);
    }
}
