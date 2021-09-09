package com.zmops.iot.enums;

import lombok.Getter;

/**
 * @author yefei
 **/
@Getter
public enum ValueType {

    CHARACTER("character", "字符型"),
    NOTSUPPORT("not supported", "不支持的类型"),
    LOG("log", "日志型"),
    FLOAT("numeric (float)", "单精度型"),
    NUMERIC("numeric (unsigned)", "数值型"),
    TEXT("text", "文本型"),
    AVG("avg", "平均值");

    String code;
    String value;

    ValueType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getVal(String status) {
        if (status == null) {
            return "";
        } else {
            for (ValueType s : ValueType.values()) {
                if (s.getCode().equals(status)) {
                    return s.getValue();
                }
            }
            return "";
        }
    }
}
