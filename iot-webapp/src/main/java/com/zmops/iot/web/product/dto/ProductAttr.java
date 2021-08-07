package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author nantian created at 2021/8/5 13:53
 * <p>
 * dto for product attribute
 */

@Data
public class ProductAttr {

    private Long attrId;

    @NotNull
    private String attrName; //属性名 非 item name

    @NotNull
    private String key;

    private String uints;

    @NotNull
    private String source; //来源：设备 trapper ，属性依赖 dependent

    private String remark;

    private Long depAttrId;

    @JsonIgnore
    private Integer zbxId;

    @NotNull
    private Long productId;

    @NotNull
    private String valueType;

    private List<ProcessingStep> processStepList; //预处理


    /**
     * 预处理步骤
     */
    public static class ProcessingStep {

        @Getter
        @Setter
        private String type;

        @Setter
        private String[] params;

        public String getParams() {
            StringBuilder paramStr = new StringBuilder();
            if (null != params && params.length > 0) {
                for (String param : params) {
                    paramStr.append(param).append("\\\\n");
                }
            }
            return paramStr.substring(0, paramStr.length() - 3);
        }
    }
}
