package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.event.applicationEvent.dto.SceneEventData;

/**
 * @author yefei
 **/
public class SceneEvent extends BaseEvent<SceneEventData> {
    public SceneEvent(Object source, SceneEventData eventData) {
        super(source, eventData);
    }
}
