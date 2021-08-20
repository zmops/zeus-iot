package com.zmops.iot.domain.sys;

import com.zmops.iot.constant.IdTypeConsts;
import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author nantian created at 2021/7/30 23:29
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "sys_role")
public class SysRole extends BaseEntity {

    @Id
    @GeneratedValue(generator = IdTypeConsts.ID_SNOW)
    Long roleId;

    String name;

    String remark;
}
