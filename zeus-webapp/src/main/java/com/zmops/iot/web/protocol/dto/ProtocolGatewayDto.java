package com.zmops.iot.web.protocol.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.protocol.ProtocolGatewayMqtt;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProtocolGatewayDto {
    private Long   protocolGatewayId;
    @NotBlank(groups = {BaseEntity.Create.class})
    private String name;
    private String protocolType;
    //    @CachedValue(type = DicType.PROTOCOL_SERVICE, fieldName = "protocolServiceName")
    private Long   protocolServiceId;
    @CachedValue(value = "PROTOCOL_GATEWAY_STATUS", fieldName = "statusName")
    private String status;
    private Long   protocolComponentId;
    private String remark;
    private Integer qos;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    Long createUser;

    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    Long updateUser;

    @CachedValue(type = DicType.Tenant, fieldName = "tenantName")
    Long tenantId;

    private List<ProtocolGatewayMqtt> protocolGatewayMqttList;
}
