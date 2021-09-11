package com.zmops.iot.web.product.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.product.ProductStatusFunction;
import com.zmops.iot.domain.product.ProductStatusFunctionRelation;
import com.zmops.iot.web.product.dto.ProductStatusJudgeRule;
import com.zmops.zeus.driver.service.ZbxDeviceStatusTrigger;
import io.ebean.DB;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public Long createDeviceStatusJudgeTrigger(ProductStatusJudgeRule judgeRule) {

        long ruleId = IdUtil.getSnowflake().nextId();
        judgeRule.setRuleId(ruleId);
        Map<String, String> rule = new HashMap<>();
        buildTriggerCreateMap(rule, judgeRule);

        String res       = deviceStatusTrigger.createDeviceStatusTrigger(rule);
        String triggerId = getTriggerId(res);

        ProductStatusFunction productStatusFunction = new ProductStatusFunction();
        BeanUtils.copyProperties(judgeRule, productStatusFunction);
        productStatusFunction.setZbxId(triggerId);
        productStatusFunction.setRuleId(ruleId);
        DB.save(productStatusFunction);

        ProductStatusFunctionRelation productStatusFunctionRelation = new ProductStatusFunctionRelation();
        productStatusFunctionRelation.setRelationId(judgeRule.getRelationId());
        productStatusFunctionRelation.setRuleId(ruleId);
        DB.save(productStatusFunctionRelation);

        return productStatusFunction.getRuleId();
    }

    /**
     * 修改 设备状态 触发器
     *
     * @param judgeRule 判断规则
     * @return Integer
     */
    public Long updateDeviceStatusJudgeTrigger(ProductStatusJudgeRule judgeRule) {
        Map<String, String> rule = new HashMap<>();
        buildTriggerCreateMap(rule, judgeRule);

        rule.put("triggerId", judgeRule.getTriggerId());
        deviceStatusTrigger.updateDeviceStatusTrigger(rule);

        ProductStatusFunction productStatusFunction = new ProductStatusFunction();
        BeanUtils.copyProperties(judgeRule, productStatusFunction);
        DB.update(productStatusFunction);

        return judgeRule.getRuleId();
    }

    /**
     * 值拷贝
     *
     * @param rule      新规则
     * @param judgeRule 规则
     */
    private void buildTriggerCreateMap(Map<String, String> rule, ProductStatusJudgeRule judgeRule) {
        rule.put("ruleId", judgeRule.getRuleId() + "");
        rule.put("deviceId", judgeRule.getRelationId());

        rule.put("ruleFunction", judgeRule.getRuleFunction());
        rule.put("ruleCondition", judgeRule.getRuleCondition());
        rule.put("itemKey", judgeRule.getProductAttrKey());

        rule.put("itemKeyRecovery", judgeRule.getProductAttrKeyRecovery());
        rule.put("ruleConditionRecovery", judgeRule.getRuleConditionRecovery());
        rule.put("ruleFunctionRecovery", judgeRule.getRuleFunctionRecovery());
    }


    private String getTriggerId(String responseStr) {
        TriggerIds ids = JSON.parseObject(responseStr, TriggerIds.class);
        if (null != ids && ids.getTriggerids().length > 0) {
            return ids.getTriggerids()[0];
        }
        return null;
    }


    @Data
    static class TriggerIds {
        String[] triggerids;
    }
}
