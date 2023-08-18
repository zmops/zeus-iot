package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.product.dto.ProductServiceDto;

/**
 * @author yefei
 **/
public class ProductServiceCreateEvent extends BaseEvent<ProductServiceDto> {
    public ProductServiceCreateEvent(Object source, ProductServiceDto eventData) {
        super(source, eventData);
    }
}
