package com.zmops.iot.web.product.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.ProductStatusFunction;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.domain.product.query.QProductStatusFunction;
import com.zmops.iot.domain.product.query.QProductStatusFunctionRelation;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductStatusJudgeRule;
import com.zmops.iot.web.product.service.ProductTriggerService;
import com.zmops.zeus.driver.service.ZbxItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author nantian created at 2021/8/3 19:45
 * <p>
 * 设备在线 离线状态 触发器
 */

@RestController
@RequestMapping("/product/status/trigger")
public class ProductStatusTriggerController {


    @Autowired
    private ZbxItem zbxItem;

    @Autowired
    private ProductTriggerService productTriggerService;

    /**
     * 离线 或者 在线触发器 信息
     *
     * @param relationId 关联产品或设备ID
     * @return ResponseData
     */
    @GetMapping("/detail")
    public ResponseData getRule(@RequestParam("relationId") String relationId) {
        return ResponseData.success(productTriggerService.getRule(relationId));
    }


    /**
     * 创建 离线 或者 在线触发器
     *
     * @param rule 在线规则
     * @return ResponseData
     */
    @PostMapping("/create")
    public ResponseData createDeviceStatusTrigger(@RequestBody @Validated(BaseEntity.Create.class) ProductStatusJudgeRule rule) {

        ProductAttribute prodAttr       = new QProductAttribute().attrId.eq(rule.getAttrId()).findOne();
        ProductAttribute prodAttrSecond = new QProductAttribute().attrId.eq(rule.getAttrIdRecovery()).findOne();

        if (null == prodAttr || null == prodAttrSecond) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }

        int count = new QProductStatusFunctionRelation().relationId.eq(rule.getRelationId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.RULE_EXISTS);
        }
        return ResponseData.success(productTriggerService.createDeviceStatusJudgeTrigger(rule));
    }


    /**
     * 修改 离线 或者 在线触发器
     *
     * @param rule 在线规则
     * @return ResponseData
     */
    @PostMapping("/update")
    public ResponseData updateDeviceStatusTrigger(@RequestBody @Validated(BaseEntity.Update.class) ProductStatusJudgeRule rule) {

        ProductAttribute prodAttr       = new QProductAttribute().attrId.eq(rule.getAttrId()).findOne();
        ProductAttribute prodAttrSecond = new QProductAttribute().attrId.eq(rule.getAttrIdRecovery()).findOne();

        if (null == prodAttr || null == prodAttrSecond) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }

        // 数据库查询 triggerId 并且赋值
        ProductStatusFunction productStatusFunction = new QProductStatusFunction().ruleId.eq(rule.getRuleId()).findOne();
        if (null == productStatusFunction) {
            throw new ServiceException(BizExceptionEnum.RULE_NOT_EXISTS);
        }
        rule.setTriggerId(productStatusFunction.getZbxId());

        return ResponseData.success(productTriggerService.updateDeviceStatusJudgeTrigger(rule));
    }

    /**
     * 删除 离线 或者 在线触发器
     *
     * @param rule 在线规则
     * @return ResponseData
     */
    @PostMapping("/delete")
    public ResponseData deleteDeviceStatusTrigger(@RequestBody @Validated(BaseEntity.Delete.class) ProductStatusJudgeRule rule) {
        productTriggerService.deleteDeviceStatusTrigger(rule.getRuleId());
        return ResponseData.success();
    }

}
