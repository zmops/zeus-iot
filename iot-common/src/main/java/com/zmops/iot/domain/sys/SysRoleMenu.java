package com.zmops.iot.domain.sys;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yefei
 * data 2021/8/3
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "sys_role_menu")
public class SysRoleMenu {

    Long roleId;

    Long menuId;
}
