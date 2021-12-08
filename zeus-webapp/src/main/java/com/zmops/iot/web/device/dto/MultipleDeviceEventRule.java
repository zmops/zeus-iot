package com.zmops.iot.web.device.dto;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.util.ToolUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

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
public class MultipleDeviceEventRule {

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Delete.class, BaseEntity.Status.class})
    private Long eventRuleId;

    private Byte eventNotify = 0;

    // 名称
    @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private String eventRuleName;

    private Byte eventLevel = 1;

    // 表达式列表
    private List<Expression> expList;

    private String expLogic = "and"; // and or

    private String remark;

    @NotEmpty(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private List<DeviceService> deviceServices;

    private List<Tag> tags;

    private String zbxId;

    private String deviceId;

    @NotBlank(groups = BaseEntity.Status.class)
    private String status;

    private String classify = "1";

    private Long tenantId;

    private String scheduleConf;

    @NotNull
    private Integer triggerType = 0;

    private Integer taskId;

    private List<TimeInterval> timeIntervals;

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

        private String attrValueType;

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
            expression.append(") ").append(condition).append(" ").append(ToolUtil.addQuotes(value));
            return expression.toString();
        }
    }


    @Setter
    @Getter
    public static class DeviceService {

        @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
        private String executeDeviceId;

        @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
        private Long serviceId;

    }

    @Getter
    @Setter
    public static class TimeInterval {
        private Integer startTime;
        private Integer endTime;

        @Override
        public String toString() {
            return "(time()>= " + startTime + " and " + " time()< " + endTime + " )";
        }
    }

}




