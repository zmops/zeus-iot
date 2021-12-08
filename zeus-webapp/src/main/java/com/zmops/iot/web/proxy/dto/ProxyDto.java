package com.zmops.iot.web.proxy.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProxyDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long id;

    @NotBlank(groups = BaseEntity.Create.class)
    private String name;

    private String mode;

    private String address;

    private String remark;

    private Integer tlsAccept;

    private String lastAccess;

    private String autoCompress;

    private String zbxId;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    Long createUser;

    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    Long updateUser;

    @CachedValue(type = DicType.Tenant, fieldName = "tenantName")
    Long tenantId;

    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> ids;
}
