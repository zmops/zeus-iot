package com.zmops.iot.web.macro.dto;

import com.zmops.iot.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author nantian created at 2021/9/18 11:47
 */

@Getter
@Setter
public class UserMacro {

    @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Get.class})
    private String deviceId; // 产品ID 或者 设备ID

    private String macro;

    private String value;

    private String description;

    @NotBlank(groups = {BaseEntity.Delete.class, BaseEntity.Update.class})
    private String hostmacroid;
}
