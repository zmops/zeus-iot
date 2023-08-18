package com.zmops.iot.web.event.applicationEvent;

import org.springframework.context.ApplicationEvent;

/**
 * @author yefei
 **/
public abstract class BaseEvent<T> extends ApplicationEvent {

    private T eventData;

    public BaseEvent(Object source, T eventData) {
        super(source);
        this.eventData = eventData;
    }

    public T getEventData() {
        return eventData;
    }
}
