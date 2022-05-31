package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.product.dto.ProductAttr;

/**
 * @author yefei
 **/
public class ProductModelCreateEvent extends BaseEvent<ProductAttr> {
    public ProductModelCreateEvent(Object source, ProductAttr eventData) {
        super(source, eventData);
    }
}
