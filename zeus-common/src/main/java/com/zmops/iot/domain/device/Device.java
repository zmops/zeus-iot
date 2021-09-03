package com.zmops.iot.domain.device;

import com.zmops.iot.constant.IdTypeConsts;
import com.zmops.iot.domain.BaseEntity;
import io.ebean.annotation.Aggregation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
    private String deviceId;

    private String name;

    private Long productId;

    private String status;

    private String type;

    private String remark;

    private String zbxId;

    private String addr;

    private String position;

    @Aggregation("count(*)")
    Long totalCount;
}
