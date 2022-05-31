package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.product.dto.ProductAttr;

/**
 * @author yefei
 **/
public class ProductAttrCreateEvent extends BaseEvent<ProductAttr> {
    public ProductAttrCreateEvent(Object source, ProductAttr eventData) {
        super(source, eventData);
    }
}
