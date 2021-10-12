package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nantian created at 2021/8/5 13:53
 * <p>
 * dto for product attribute
 */

@Data
public class ProductAttr {

    @NotNull(groups = BaseEntity.Update.class)
    private Long attrId;

    //属性名 非 item name
    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String attrName;

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String key;

    private String units;

    //来源：设备 trapper ，属性依赖 dependent
    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String source;

    private Byte eventLevel = 1;

    private String remark;

    private Long depAttrId;

    @JsonIgnore
    private String masterItemId;

    @JsonIgnore
    private String zbxId;

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String productId;

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String valueType;

    private String valuemapid;

    private List<ProductTag.Tag> tags;

    //预处理
    private List<ProcessingStep> processStepList;

    @NotEmpty(groups = BaseEntity.Delete.class)
    private List<Long> attrIds;

    private Integer delay;
    //取数间隔单位
    private String unit;

    LocalDateTime createTime;
    LocalDateTime updateTime;
    Long createUser;
    Long updateUser;

    /**
     * 预处理步骤
     */
    public static class ProcessingStep {

        @Getter
        @Setter
        private String type;

        @Setter
        private String[] params;

        @Getter
        @Setter
        private String errorHandler;

        @Getter
        @Setter
        private String errorHandlerParams;

        public String getParams() {
            StringBuilder paramStr = new StringBuilder();
            if (null != params && params.length > 0) {
                for (String param : params) {
                    paramStr.append(param).append("\\\\n");
                }
                return paramStr.substring(0, paramStr.length() - 3);
            }
            return "";
        }
    }
}
