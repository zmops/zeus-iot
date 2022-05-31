package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.product.dto.ProductServiceDto;

/**
 * @author yefei
 **/
public class ProductServiceUpdateEvent extends BaseEvent<ProductServiceDto> {
    public ProductServiceUpdateEvent(Object source, ProductServiceDto eventData) {
        super(source, eventData);
    }
}
