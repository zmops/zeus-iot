package com.zmops.iot.web.product.dto;

import com.zmops.iot.domain.product.ProductEventExpression;
import com.zmops.iot.domain.product.ProductEventService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * @author nantian created at 2021/9/14 14:47
 * <p>
 * 告警规则
 */

@Getter
@Setter
public class ProductEventRuleDto {

    private Long eventRuleId;

    private Byte eventNotify;
    private String eventRuleName;

    private Byte eventLevel;

    private List<ProductEventExpression> expList;

    private String expLogic;

    private String remark;

    private List<ProductEventService> deviceServices;

    private List<Tag> tags;

    private String zbxId;

    private String productId;

    private String status;

    private String inherit;

    private String inheritProductId;

    @Data
    public static class Tag {

        @Max(20)
        private String tag;

        @Max(50)
        private String value;
    }


}




