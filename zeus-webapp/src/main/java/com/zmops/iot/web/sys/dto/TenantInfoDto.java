package com.zmops.iot.web.sys.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yefei
 */
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class TenantInfoDto implements Serializable {


    /**
     * 主键id
     */
    private Long tenantId;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户管理员账号
     */
    private String account;


    /**
     * 租户联系人
     */
    private String contact;

    /**
     * 租户联系人电话
     */
    private String phone;

    private int userNum;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    Long createUser;

    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    Long updateUser;

    @CachedValue(value = "STATUS", fieldName = "statusName")
    private String status;
}
