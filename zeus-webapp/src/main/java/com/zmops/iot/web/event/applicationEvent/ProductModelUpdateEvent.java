package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.product.dto.ProductAttr;

/**
 * @author yefei
 **/
public class ProductModelUpdateEvent extends BaseEvent<ProductAttr> {
    public ProductModelUpdateEvent(Object source, ProductAttr eventData) {
        super(source, eventData);
    }
}
