package com.zmops.iot.domain.device;

import com.zmops.iot.domain.BaseEntity;
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
@Table(name = "device")
@Entity
public class Device extends BaseEntity {
    @Id
    private Long deviceId;

    private String name;

    private Long productId;

    private Long deviceGroupId;

    private String status;

    private String type;

    private String remark;

}
