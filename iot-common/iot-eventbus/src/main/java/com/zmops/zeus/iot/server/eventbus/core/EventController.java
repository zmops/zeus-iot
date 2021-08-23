package com.zmops.zeus.iot.server.eventbus.core;

import java.util.Collection;

/**
 * EventBus 控制器
 *
 * @author nantian
 */
public interface EventController {

    void register(Object object);

    void unregister(Object object);

    void post(Object event);

    void post(Collection<? extends Object> event);

    void postEvent(Event event);

    void postEvent(Collection<? extends Event> events);
}