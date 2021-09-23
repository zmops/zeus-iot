package com.zmops.iot.web.device.dto;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author nantian created at 2021/9/14 14:47
 * <p>
 * 告警规则
 */

@Getter
@Setter
public class DeviceEventRule {

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Delete.class, BaseEntity.Status.class})
    private Long eventRuleId;

    @NotNull(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private Byte eventNotify; // 0 否 1 是，默认 1

    // 告警规则名称
    @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private String eventRuleName;

    // 告警规则级别
    @NotNull(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private Byte eventLevel;

    // 表达式列表
    @Valid
    @NotEmpty(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private List<Expression> expList;

    @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private String expLogic; // and or

    private String remark;

    private List<DeviceService> deviceServices;

    private List<Tag> tags;

    @NotNull(groups = BaseEntity.Update.class)
    private Integer zbxId;

    @NotBlank(groups = {BaseEntity.Status.class, BaseEntity.Delete.class})
    private String deviceId;

    @NotBlank(groups = BaseEntity.Status.class)
    private String status;

    private String classify = "0";

    @Data
    public static class Tag {

        @Max(20)
        private String tag;

        @Max(50)
        private String value;
    }

    @Getter
    @Setter
    // 告警表达式 构成
    public static class Expression {

        @NotNull(groups = BaseEntity.Update.class)
        private Long eventExpId;

        @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
        private String function; // last avg max min sum change nodata

        private String scope; // s m h T

        @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
        private String condition; // > < = <> >= <=

        @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
        private String value;

        @NotNull(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
        private String productAttrKey; // 产品属性 Key

        private String deviceId; // 设备ID

        private String unit;

        private Long productAttrId;

        private String productAttrType;

        private String period;

        @Override
        public String toString() {
            StringBuilder expression = new StringBuilder();
            expression.append(function);
            expression.append("(/");
            expression.append(deviceId);
            expression.append("/");
            expression.append(productAttrKey);

            if (StringUtils.isNotBlank(scope)) {
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

        private String deviceId;

        private String executeDeviceId;

        private Long serviceId;
    }

}




