package com.zmops.iot.web.analyse.enums;

import lombok.Getter;

/**
 * @author yefei
 **/
public enum MemoryUtilizationEnum {
    utilization("vm.memory.utilization", "memoryUtilization"),
    total("vm.memory.size[total]", "memoryTotal"),
    available("vm.memory.size[available]", "memoryAvailable");

    @Getter
    String code;
    @Getter
    String message;

    MemoryUtilizationEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getDescription(String status) {
        if (status == null) {
            return "";
        } else {
            for (MemoryUtilizationEnum s : MemoryUtilizationEnum.values()) {
                if (s.getCode().equals(status)) {
                    return s.getMessage();
                }
            }
            return "";
        }
    }
}
