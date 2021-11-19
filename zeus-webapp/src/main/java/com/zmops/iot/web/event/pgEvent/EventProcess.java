package com.zmops.iot.web.event.pgEvent;

import com.zmops.iot.web.event.pgEvent.dto.EventDataDto;

/**
 * @author yefei
 **/
public interface EventProcess {

    void process(EventDataDto eventData);

    boolean checkTag(String tag);
}
