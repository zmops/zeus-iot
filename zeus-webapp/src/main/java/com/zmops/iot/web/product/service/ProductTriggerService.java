package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.web.product.dto.ProductStatusJudgeRule;
import com.zmops.zeus.driver.service.ZbxDeviceStatusTrigger;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> rule = new HashMap<>();
        buildTriggerCreateMap(rule, judgeRule);

        String res = deviceStatusTrigger.createDeviceStatusTrigger(rule);
        return getTriggerId(res);
    }

    /**
     * 修改 设备状态 触发器
     *
     * @param judgeRule 判断规则
     * @return Integer
     */
    public Integer updateDeviceStatusJudgeTrigger(ProductStatusJudgeRule judgeRule) {
        Map<String, String> rule = new HashMap<>();
        buildTriggerCreateMap(rule, judgeRule);

        rule.put("triggerId", judgeRule.getTriggerId());
        String res = deviceStatusTrigger.createDeviceStatusTrigger(rule);

        return getTriggerId(res);
    }

    /**
     * 值拷贝
     *
     * @param rule      新规则
     * @param judgeRule 规则
     */
    private void buildTriggerCreateMap(Map<String, String> rule, ProductStatusJudgeRule judgeRule) {
        rule.put("ruleId", judgeRule.getRuleId());
        rule.put("deviceId", judgeRule.getDeviceId());

        rule.put("ruleFunction", judgeRule.getRuleFunction());
        rule.put("ruleCondition", judgeRule.getRuleCondition());
        rule.put("itemKey", judgeRule.getProductAttrKey());

        rule.put("itemKeySecond", judgeRule.getProductAttrKeySecond());
        rule.put("ruleConditionSecond", judgeRule.getRuleConditionSecond());
        rule.put("ruleFunctionSecond", judgeRule.getRuleFunctionSecond());
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
