package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.product.dto.ProductEventRule;

/**
 * @author yefei
 **/
public class ProductEventTriggerUpdateEvent extends BaseEvent<ProductEventRule> {
    public ProductEventTriggerUpdateEvent(Object source, ProductEventRule eventData) {
        super(source, eventData);
    }
}
