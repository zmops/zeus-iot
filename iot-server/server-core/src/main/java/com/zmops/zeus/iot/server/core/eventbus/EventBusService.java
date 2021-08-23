package com.zmops.zeus.iot.server.core.eventbus;

import com.zmops.zeus.iot.server.action.bean.ActionParam;
import com.zmops.zeus.iot.server.eventbus.core.EventControllerFactory;
import com.zmops.zeus.iot.server.library.module.Service;

/**
 * @author nantian created at 2021/8/24 0:12
 * <p>
 * EventBus post service
 */
public class EventBusService implements Service {

    private final EventControllerFactory eventControllerFactory;

    public EventBusService(EventControllerFactory eventControllerFactory) {
        this.eventControllerFactory = eventControllerFactory;
    }

    public void postExecuteActionMsg(String identifier, ActionParam param) {
        eventControllerFactory.getAsyncController(identifier).post(param);
    }
}
