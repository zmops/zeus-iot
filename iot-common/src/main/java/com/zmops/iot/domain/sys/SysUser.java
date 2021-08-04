package com.zmops.iot.domain.sys;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/7/30 20:31
 */

@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "sys_user")
@Entity
public class SysUser extends BaseEntity {

    @Id
    Long userId;

    String account;

    String password;

    String status;

    String email;

    String name;

    String phone;

    String salt;

    Long userGroupId;

    Long roleId;

    String zbxToken;

    String zbxId;
}
