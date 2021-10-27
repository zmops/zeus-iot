package com.zmops.iot.web.event;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.web.event.dto.EventDataDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author yefei
 **/
@Slf4j
@Component(value = "EventDataProcess")
public class EventDataProcess implements Processor {

    @Autowired
    Collection<EventProcess> eventProcessList;

    @Override
    public void process(Exchange exchange) throws Exception {
        String inputContext = exchange.getIn().getBody().toString();
        String[] split = inputContext.split("分隔线");
        if (split.length != 2) {
            return;
        }

        System.out.println("-------------------" + split[1]);
        EventDataDto eventData = JSONObject.parseObject(split[1], EventDataDto.class);

        String objectid = eventData.getObjectid();

        eventProcessList.forEach(eventProcess -> {
            if(eventProcess.checkTag(eventData.getTag())){
                eventProcess.process(objectid,eventData.getName());
            }
        });

    }

}
