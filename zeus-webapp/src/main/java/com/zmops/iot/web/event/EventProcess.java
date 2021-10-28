package com.zmops.iot.web.event;

import com.zmops.iot.web.event.dto.EventDataDto;

/**
 * @author yefei
 **/
public interface EventProcess {

    void process(EventDataDto eventData);

    boolean checkTag(String tag);
}
