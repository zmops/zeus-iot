package com.zmops.iot.web.event.service;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.Forest;
import com.zmops.iot.async.executor.Async;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.product.ProductEventService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.service.MultipleDeviceEventRuleService;
import com.zmops.iot.web.device.service.work.DeviceServiceLogWorker;
import com.zmops.iot.web.device.service.work.ScenesLogWorker;
import com.zmops.iot.web.event.EventProcess;
import com.zmops.iot.web.event.dto.EventDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
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

        eventRuleService.execute(Long.parseLong(eventData.getName()));
    }

    @Override
    public boolean checkTag(String tag) {
        return "__scene__".equals(tag);
    }

}
