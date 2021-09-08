package com.zmops.iot.domain.device;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "device_online_report")
@Entity
public class DeviceOnlineReprot {

    @Id
    private Long id;

    private String createTime;

    private Integer online;

    private Integer offline;

}
