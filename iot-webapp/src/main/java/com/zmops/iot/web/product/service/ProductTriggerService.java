package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.web.product.dto.ProductStatusJudgeRule;
import com.zmops.zeus.driver.service.ZbxDeviceStatusTrigger;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.zmops.iot.web.product.dto.ProductStatusJudgeRule.OFF_LINE;
import static com.zmops.iot.web.product.dto.ProductStatusJudgeRule.ON_LINE;
import static com.zmops.iot.web.product.dto.ProductStatusJudgeRule.NODATA;
import static com.zmops.iot.web.product.dto.ProductStatusJudgeRule.LASTDATA;

/**
 * @author nantian created at 2021/8/10 17:55
 * <p>
 * <p>
 * 创建 设备 状态触发器
 */

@Service
public class ProductTriggerService {


    @Autowired
    private ZbxDeviceStatusTrigger deviceStatusTrigger;


    /**
     * 创建 设备状态 触发器
     *
     * @param judgeRule 判断规则
     * @return Integer
     */
    public Integer createDeviceStatusJudgeTrigger(ProductStatusJudgeRule judgeRule) {

        String triggerName = judgeRule.getTriggerName();
        String hostName    = judgeRule.getHostName();
        String itemKey     = judgeRule.getItemKey();
        String ruleValue   = judgeRule.getRuleValue();

        if (NODATA.equals(judgeRule.getRuleFunction())) {
            // 上线触发器
            if (ON_LINE.equals(judgeRule.getRuleType())) {
                return getTriggerId(deviceStatusTrigger.nodataOnline(hostName, itemKey, ruleValue, triggerName));
            }

            if (OFF_LINE.equals(judgeRule.getRuleType())) {
                return getTriggerId(deviceStatusTrigger.nodataOffline(hostName, itemKey, ruleValue, triggerName));
            }
        }

        if (LASTDATA.equals(judgeRule.getRuleFunction())) {
            return getTriggerId(deviceStatusTrigger.lastValueJudge(hostName, itemKey, ruleValue, triggerName));
        }

        return null;
    }

    private Integer getTriggerId(String responseStr) {
        TriggerIds ids = JSON.parseObject(responseStr, TriggerIds.class);
        if (null != ids && ids.getTriggerids().length > 0) {
            return ids.getTriggerids()[0];
        }
        return null;
    }


    @Data
    static class TriggerIds {
        Integer[] triggerids;
    }
}
