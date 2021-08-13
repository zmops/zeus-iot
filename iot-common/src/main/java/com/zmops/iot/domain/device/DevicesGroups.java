package com.zmops.iot.domain.device;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "sys_usrgrp_devicegrp")
@Entity
public class DevicesGroups {
    Long userGroupId;
    Long deviceGroupId;
}
