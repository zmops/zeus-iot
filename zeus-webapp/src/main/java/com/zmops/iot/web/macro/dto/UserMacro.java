package com.zmops.iot.web.macro.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/9/18 11:47
 */

@Getter
@Setter
public class UserMacro {

    private Long deviceId; // 产品ID 或者 设备ID

    private String macro;

    private String value;

    private String description;

    private String macro_old;
}
