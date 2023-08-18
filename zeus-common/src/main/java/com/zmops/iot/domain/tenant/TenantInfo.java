package com.zmops.iot.domain.tenant;


import com.zmops.iot.constant.IdTypeConsts;
import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "tenant_info")
@Entity
public class TenantInfo extends BaseEntity {


    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = IdTypeConsts.ID_SNOW)
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

    private String status;

}
