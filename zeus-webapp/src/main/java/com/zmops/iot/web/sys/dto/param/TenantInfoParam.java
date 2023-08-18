package com.zmops.iot.web.sys.dto.param;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 * @author yefei
 */
@Data
public class TenantInfoParam extends BaseQueryParam {

    /**
     * 主键id
     */
    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Delete.class})
    private Long tenantId;

    /**
     * 租户名称
     */
    @NotBlank(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String name;

    private String status;
    /**
     * 备注
     */
    private String remark;

    /**
     * 租户管理员账号
     */
    @NotBlank(groups = BaseEntity.Create.class)
    private String account;

    /**
     * 租户管理员账号密码
     */
    @NotBlank(groups = BaseEntity.Create.class)
    private String password;

    /**
     * 租户联系人
     */
    private String contact;

    /**
     * 租户联系人电话
     */
    @Pattern(regexp = "^[1][3,4,5,7,8][0-9]{9}$",message = "电话号码格式不正确")
    private String phone;


}
