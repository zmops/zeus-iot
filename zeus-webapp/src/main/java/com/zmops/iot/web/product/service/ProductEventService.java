package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventExpression;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductEventDto;
import com.zmops.iot.web.product.dto.param.EventParm;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/9/15 13:59
 */

@Slf4j
@Service
public class ProductEventService {

    @Autowired
    private ZbxTrigger zbxTrigger;


    /**
     * 保存触发器
     *
     * @param productEvent productEvent
     */
    public void createProductEventRule(ProductEvent productEvent) {
        productEvent.save();
    }


    public void createProductEventExpression(List<ProductEventExpression> expressionList) {
        DB.saveAll(expressionList);
    }


    public void createProductEventTags(String triggerId, Map<String, String> tags) {
        String res = zbxTrigger.triggerTagCreate(Long.valueOf(triggerId), tags);
        log.debug(res);
    }


    /**
     * 创建 触发器
     *
     * @param triggerName 触发器名称
     * @param expression  表达式
     * @param level       告警等级
     * @return 触发器ID
     */
    public String createZbxTrigger(String triggerName, String expression, Byte level) {
        String res = zbxTrigger.triggerCreate(triggerName, expression, level);
        return JSON.parseObject(res, TriggerIds.class).getTriggerids()[0];
    }

    /**
     * 触发器 分页列表
     *
     * @param eventParm
     * @return
     */
    public Pager<ProductEventDto> getEventByPage(EventParm eventParm) {
        QProductEvent query = new QProductEvent();
        if (ToolUtil.isNotEmpty(eventParm.getEventRuleName())) {
            query.eventRuleName.contains(eventParm.getEventRuleName());
        }
        if (ToolUtil.isNotEmpty(eventParm.getProdId())) {
            List<Long> eventRuleIdList = new QProductEventExpression().select(QProductEventExpression.alias().eventRuleId).productId.eq(eventParm.getProdId()).findSingleAttributeList();
            if (ToolUtil.isNotEmpty(eventRuleIdList)) {
                query.eventRuleId.in(eventRuleIdList);
            }
        }
        List<ProductEventDto> list = query.setFirstRow((eventParm.getPage() - 1) * eventParm.getMaxRow())
                .setMaxRows(eventParm.getMaxRow()).orderBy(" create_time desc").asDto(ProductEventDto.class).findList();
        return new Pager<>(list, query.findCount());
    }


    @Data
    static class TriggerIds {
        private String[] triggerids;
    }
}
