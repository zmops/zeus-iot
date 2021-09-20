package com.zmops.iot.web.trigger;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.trigger.dto.TriggerStatusDto;
import com.zmops.zeus.driver.service.ZbxTrigger;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author nantian created at 2021/9/20 14:53
 * <p>
 * 通用触发器相关
 */

@RestController
@RequestMapping("/event/trigger")
public class EventTriggerController {


    @Autowired
    private ZbxTrigger zbxTrigger;

    /**
     * 修改 设备 产品 告警规则启用禁用
     *
     * @return
     */
    @RequestMapping("/status/update")
    public ResponseData updateTriggerStatus(@RequestBody @Valid TriggerStatusDto statusDto) {
        String res = zbxTrigger.triggerStatusUpdate(statusDto.getTriggerId(), statusDto.getStatus());

//        DB.update(ProductEvent.class)
//                .where().eq("eventRuleId", statusDto.getEventRuleId())
//                .asUpdate().set("status", statusDto.getStatus()).update(); // 0 启用，1 禁用


        return ResponseData.success(JSON.parseObject(res, TriggerIds.class).getTriggerids()[0]);
    }

    @Getter
    @Setter
    static class TriggerIds {
        private String[] triggerids;
    }
}
