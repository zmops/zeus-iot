package com.zmops.iot.domain.device;

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
@Table(name = "device_group")
@Entity
public class DeviceGroup extends BaseEntity {

    @Id
    Long deviceGroupId;

    String name;

    String zbxId;

    String remark;

}
