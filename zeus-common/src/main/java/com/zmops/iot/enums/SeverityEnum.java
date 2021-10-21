package com.zmops.iot.enums;

import lombok.Getter;

/**
 * @author yefei
 **/
@Getter
public enum SeverityEnum {

    INFO("信息", "1"),
    WARN("低级", "2"),
    ALARM("中级", "3"),
    HIGH("高级", "4"),
    DISASTER("紧急", "5");

    String code;
    String value;

    SeverityEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getVal(String status) {
        if (status == null) {
            return "";
        } else {
            for (SeverityEnum s : SeverityEnum.values()) {
                if (s.getCode().equals(status)) {
                    return s.getValue();
                }
            }
            return "0";
        }
    }
}
