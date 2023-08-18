package com.zmops.iot.web.analyse.enums;

import lombok.Getter;

/**
 * @author yefei
 **/
public enum ProcessEnum {
    num("proc.num", "num"),
    run("proc.num[,,run]", "run"),
    max("kernel.maxproc", "max");

    @Getter
    String code;
    @Getter
    String message;

    ProcessEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getDescription(String status) {
        if (status == null) {
            return "";
        } else {
            for (ProcessEnum s : ProcessEnum.values()) {
                if (s.getCode().equals(status)) {
                    return s.getMessage();
                }
            }
            return "";
        }
    }
}
