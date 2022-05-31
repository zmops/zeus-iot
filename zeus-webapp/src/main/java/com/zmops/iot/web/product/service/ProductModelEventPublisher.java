package com.zmops.iot.web.product.service;

import com.zmops.iot.web.event.applicationEvent.ProductModelCreateEvent;
import com.zmops.iot.web.event.applicationEvent.ProductModelUpdateEvent;
import com.zmops.iot.web.product.dto.ProductAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class ProductModelEventPublisher {

    @Autowired
    ApplicationEventPublisher publisher;

    @Async
    public void productModelCreateEventPublish(ProductAttr productAttr) {
        publisher.publishEvent(new ProductModelCreateEvent(this, productAttr));
    }

    @Async
    public void productModelUpdateEventPublish(ProductAttr productAttr) {
        publisher.publishEvent(new ProductModelUpdateEvent(this, productAttr));
    }
}
