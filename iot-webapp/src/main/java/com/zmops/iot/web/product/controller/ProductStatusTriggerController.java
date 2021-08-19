package com.zmops.iot.web.product.controller;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductStatusJudgeRule;
import com.zmops.iot.web.product.service.ProductTriggerService;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.service.ZbxItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.zmops.iot.web.product.dto.ProductStatusJudgeRule.ON_LINE;
import static com.zmops.iot.web.product.dto.ProductStatusJudgeRule.OFF_LINE;

/**
 * @author nantian created at 2021/8/3 19:45
 * <p>
 * 设备在线状态 触发器
 */

@RestController
@RequestMapping("/product/trigger/status")
public class ProductStatusTriggerController {


    @Autowired
    private ZbxItem zbxItem;

    @Autowired
    private ProductTriggerService productTriggerService;


    /**
     * 创建 离线 或者 在线触发器
     *
     * @param rule 在线规则
     * @return ResponseData
     */
    @PostMapping("/create")
    public ResponseData createOfflineTrigger(@RequestBody @Valid ProductStatusJudgeRule rule) {

        // nodata(/sanshi-host/sender.a,1m)=1
        // 查询 itemid itemkey 和 template name
        ProductAttribute prodAttr = new QProductAttribute().attrId.eq(rule.getProductAttrId()).findOne();
        if (null == prodAttr) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }

        List<ZbxItemInfo> itemInfo = JSON.parseObject(zbxItem.getItemInfo(prodAttr.getZbxId(), null), List.class);

        String key = itemInfo.get(0).getKey_(); // item key


        productTriggerService.createDeviceStatusJudgeTrigger(rule);


        return ResponseData.success();
    }
}
