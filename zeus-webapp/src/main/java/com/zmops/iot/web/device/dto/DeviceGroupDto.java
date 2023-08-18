package com.zmops.iot.web.device.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 设备组传输bean
 *
 * @author yefei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = CachedValueFilter.class)
public class DeviceGroupDto {

    private Long deviceGroupId;

    private String name;

    private String remark;

    @CachedValue(type = DicType.Tenant, fieldName = "tenantName")
    private Long tenantId;

    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    private Long          createUser;
    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    private Long          updateUser;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
