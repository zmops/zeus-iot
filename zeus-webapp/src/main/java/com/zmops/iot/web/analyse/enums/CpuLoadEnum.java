package com.zmops.iot.web.analyse.enums;

import lombok.Getter;

/**
 * @author yefei
 **/
public enum CpuLoadEnum {
    avg1("system.cpu.load[all,avg1]", "avg1"),
    avg5("system.cpu.load[all,avg5]", "avg5"),
    avg15("system.cpu.load[all,avg15]", "avg15");

    @Getter
    String code;
    @Getter
    String message;

    CpuLoadEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getDescription(String status) {
        if (status == null) {
            return "";
        } else {
            for (CpuLoadEnum s : CpuLoadEnum.values()) {
                if (s.getCode().equals(status)) {
                    return s.getMessage();
                }
            }
            return "";
        }
    }
}
