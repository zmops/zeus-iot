package com.zmops.iot.web.product.dto;

import com.zmops.iot.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/9/14 14:47
 * <p>
 * 告警规则
 */

@Getter
@Setter
public class ProductEventRule {

    private String eventRuleId;

    @NotNull(groups = BaseEntity.Create.class)
    private Byte eventNotify; // 0 否 1 是，默认 1

    // 告警规则名称
    @NotBlank(groups = BaseEntity.Create.class)
    private String eventRuleName;

    // 告警规则级别
    @NotNull(groups = BaseEntity.Create.class)
    private Byte eventLevel;

    // 表达式列表
    @Valid
    @NotEmpty(groups = BaseEntity.Create.class)
    private List<Expression> expList;

    @NotBlank(groups = BaseEntity.Create.class)
    private String expLogic; // and or

    private String remark;

    private List<DeviceService> deviceServices;

    private Map<String, String> tags;


    @Getter
    @Setter
    // 告警表达式 构成
    public static class Expression {

        @NotBlank(groups = BaseEntity.Create.class)
        private String function; // last avg max min sum change nodata

        private String scope; // s m h T

        @NotBlank(groups = BaseEntity.Create.class)
        private String condition; // > < = <> >= <=

        @NotBlank(groups = BaseEntity.Create.class)
        private String value;

        @NotNull(groups = BaseEntity.Create.class)
        private String productAttrKey; // 产品属性 Key

        @NotBlank(groups = BaseEntity.Create.class)
        private String productId; // 产品ID

        @Override
        public String toString() {
            StringBuilder expression = new StringBuilder();
            expression.append(function);
            expression.append("(/");
            expression.append(productId);
            expression.append("/");
            expression.append(productAttrKey);

            if (null != scope) {
                expression.append(", ");
                expression.append(scope);
            }
            expression.append(") ").append(condition).append(" ").append(value);
            return expression.toString();
        }
    }


    @Setter
    @Getter
    public static class DeviceService {

        private Long deviceId;

        private Long serviceId;
    }

}




