package com.zmops.iot.web.product.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.ProductStatusFunction;
import com.zmops.iot.domain.product.ProductStatusFunctionRelation;
import com.zmops.iot.domain.product.query.QProductStatusFunction;
import com.zmops.iot.domain.product.query.QProductStatusFunctionRelation;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductStatusFunctionDto;
import com.zmops.iot.web.product.dto.ProductStatusJudgeRule;
import com.zmops.iot.web.product.dto.ZbxTriggerInfo;
import com.zmops.zeus.driver.service.ZbxDeviceStatusTrigger;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private ZbxTrigger zbxTrigger;

    /**
     * 离线 或者 在线触发器 信息
     *
     * @param relationId 在线规则
     * @return ResponseData
     */
    public ProductStatusFunctionDto getRule(String relationId) {
        String sql = "select s.*,p.name attrName,p2.name attrNameRecovery,p.units,p2.units unitsRecovery " +
                " from product_status_function s" +
                " LEFT JOIN product_attribute p on p.attr_id = s.attr_id" +
                " LEFT JOIN product_attribute p2 on p2.attr_id = s.attr_id_recovery" +
                " where s.rule_id in (select rule_id from product_status_function_relation where relation_id = :relationId)";
        return DB.findDto(ProductStatusFunctionDto.class, sql).setParameter("relationId", relationId).findOne();
    }


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
        //step 1:保存到zbx 建立上线及下线规则
        String res = deviceStatusTrigger.createDeviceStatusTrigger(judgeRule.getRuleId() + "", judgeRule.getRelationId(),
                judgeRule.getProductAttrKey(), judgeRule.getRuleCondition() + judgeRule.getUnit(), judgeRule.getRuleFunction(), judgeRule.getProductAttrKeyRecovery(),
                judgeRule.getRuleConditionRecovery() + judgeRule.getUnitRecovery(), judgeRule.getRuleFunctionRecovery());

        String[] triggerIds = getTriggerId(res);

        //step 2:保存规则
        ProductStatusFunction productStatusFunction = new ProductStatusFunction();
        BeanUtils.copyProperties(judgeRule, productStatusFunction);
        productStatusFunction.setRuleId(ruleId);
        DB.save(productStatusFunction);

        //step 3:保存规则与产品的关联关系
        ProductStatusFunctionRelation productStatusFunctionRelation = new ProductStatusFunctionRelation();
        productStatusFunctionRelation.setRelationId(judgeRule.getRelationId());
        productStatusFunctionRelation.setRuleId(ruleId);
        //对应zbx下线规则ID
        productStatusFunctionRelation.setZbxId(triggerIds[0]);
        //对应zbx上线规则ID
        productStatusFunctionRelation.setZbxIdRecovery(triggerIds[1]);
        DB.save(productStatusFunctionRelation);

        //step 4:同步到设备
        String relationId = judgeRule.getRelationId();
        if (ToolUtil.isNum(relationId)) {
            //查询出继承了此产品的设备
            String sql = "select device_id from device where product_id = :productId and device_id not in (select relation_id from product_status_function_relation where inherit='0')";
            List<DeviceDto> deviceDtoList = DB.findDto(DeviceDto.class, sql).setParameter("productId", Long.parseLong(relationId)).findList();
            if (ToolUtil.isEmpty(deviceDtoList)) {
                return productStatusFunction.getRuleId();
            }

            //从Zbx 查询出所有设备的  名称是 judgeRule.getRuleId() 的触发器
            String triggerRes = zbxTrigger.triggerGetByName(judgeRule.getRuleId() + "");
            if (ToolUtil.isEmpty(triggerRes)) {
                return productStatusFunction.getRuleId();
            }
            List<ZbxTriggerInfo> zbxTriggerInfoList = JSONObject.parseArray(triggerRes, ZbxTriggerInfo.class);
            Map<String, String> hostTriggerMap = zbxTriggerInfoList.parallelStream().filter(o -> o.getTags().parallelStream().anyMatch(t -> "__offline__".equals(t.getTag())))
                    .collect(Collectors.toMap(o -> o.getHosts().get(0).getHost(), ZbxTriggerInfo::getTriggerid));
            Map<String, String> hostRecoveryTriggerMap = zbxTriggerInfoList.parallelStream().filter(o -> o.getTags().parallelStream().anyMatch(t -> "__online__".equals(t.getTag())))
                    .collect(Collectors.toMap(o -> o.getHosts().get(0).getHost(), ZbxTriggerInfo::getTriggerid));

            //保存 设备与规则的关系
            deviceDtoList.forEach(deviceDto -> {
                String zbxId = Optional.ofNullable(hostTriggerMap.get(deviceDto.getDeviceId())).orElse("");
                String zbxIdRecovery = Optional.ofNullable(hostRecoveryTriggerMap.get(deviceDto.getDeviceId())).orElse("");

                DB.sqlUpdate("insert into product_status_function_relation (relation_id,rule_id,inherit,zbx_id,zbx_id_recovery) SELECT :deviceId,rule_id,1,:zbxId,:zbxIdRecovery from product_status_function_relation where relation_id=:relationId")
                        .setParameter("deviceId", deviceDto.getDeviceId()).setParameter("relationId", judgeRule.getRelationId() + "")
                        .setParameter("zbxId", zbxId)
                        .setParameter("zbxIdRecovery", zbxIdRecovery)
                        .execute();
            });
        }

        return productStatusFunction.getRuleId();
    }

    /**
     * 修改 触发器
     *
     * @param judgeRule 判断规则
     * @return Integer
     */
    public Long updateDeviceStatusJudgeTrigger(ProductStatusJudgeRule judgeRule) {

        ProductStatusFunctionRelation relation = new QProductStatusFunctionRelation().ruleId.eq(judgeRule.getRuleId()).relationId.eq(judgeRule.getRelationId()).findOne();
        if (null == relation) {
            return judgeRule.getRuleId();
        }
        deviceStatusTrigger.updateDeviceStatusTrigger(relation.getZbxId(), judgeRule.getRuleId() + "", judgeRule.getRelationId(),
                judgeRule.getProductAttrKey(), judgeRule.getRuleCondition() + judgeRule.getUnit(), judgeRule.getRuleFunction(), judgeRule.getProductAttrKeyRecovery(),
                judgeRule.getRuleConditionRecovery() + judgeRule.getUnitRecovery(), judgeRule.getRuleFunctionRecovery(), relation.getZbxIdRecovery());

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


    private String[] getTriggerId(String responseStr) {
        TriggerIds ids = JSON.parseObject(responseStr, TriggerIds.class);
        if (null == ids || ids.getTriggerids().length != 2) {
            throw new ServiceException(BizExceptionEnum.ZBX_CALL_ERR);
        }
        return ids.getTriggerids();
    }

    /**
     * 删除 离线 或者 在线触发器
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceStatusTrigger(Long ruleId) {
        new QProductStatusFunctionRelation().ruleId.eq(ruleId).delete();

        new QProductStatusFunction().ruleId.eq(ruleId).delete();
    }


    @Data
    static class TriggerIds {
        String[] triggerids;
    }


}
