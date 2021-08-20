package com.zmops.iot.domain.sys;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei created at 2021/7/30 20:31
 */

@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "sys_user_group")
@Entity
public class SysUserGroup extends BaseEntity {

    @Id
    Long userGroupId;

    String groupName;

    String zbxId;

    String status;

    String remark;

}
