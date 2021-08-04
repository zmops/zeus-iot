package com.zmops.iot.web.sys.dto;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yefei
 **/
@Data
public class SysRoleDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long roleId;

    private String name;

    private String remark;
}
