package com.zmops.iot.web.event.pgEvent.service;

import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.service.MultipleDeviceEventRuleService;
import com.zmops.iot.web.event.pgEvent.EventProcess;
import com.zmops.iot.web.event.pgEvent.dto.EventDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yefei
 *
 * 场景触发
 **/
@Slf4j
@Component
public class SceneEventProcess implements EventProcess {

    @Autowired
    MultipleDeviceEventRuleService eventRuleService;

    @Override
    public void process(EventDataDto eventData) {
        log.debug("--------scene event----------ruleId:{}", eventData.getName());

        if (ToolUtil.isEmpty(eventData.getName())) {
            return;
        }

        eventRuleService.execute(Long.parseLong(eventData.getName()), "自动", null);
    }

    @Override
    public boolean checkTag(String tag) {
        return "__scene__".equals(tag);
    }

}
