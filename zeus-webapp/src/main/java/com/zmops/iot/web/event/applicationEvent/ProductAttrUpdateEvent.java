package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.product.dto.ProductAttr;

/**
 * @author yefei
 **/
public class ProductAttrUpdateEvent extends BaseEvent<ProductAttr> {
    public ProductAttrUpdateEvent(Object source, ProductAttr eventData) {
        super(source, eventData);
    }
}
