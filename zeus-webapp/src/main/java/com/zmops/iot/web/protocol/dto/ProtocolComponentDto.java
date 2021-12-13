package com.zmops.iot.web.protocol.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProtocolComponentDto {
    private Long   protocolComponentId;
    private String name;
    private String effectProxy;
    @CachedValue(value = "PROTOCOL_COMPONENT_STATUS", fieldName = "statusName")
    private String status;
    private String remark;
    private String fileName;
    LocalDateTime createTime;

    LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    Long createUser;

    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    Long updateUser;

    @CachedValue(type = DicType.Tenant, fieldName = "tenantName")
    Long tenantId;
}
