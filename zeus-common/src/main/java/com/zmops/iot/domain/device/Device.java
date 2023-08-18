package com.zmops.iot.domain.device;

import com.zmops.iot.domain.BaseEntity;
import io.ebean.annotation.Aggregation;
import io.ebean.annotation.TenantId;
import io.ebean.annotation.WhoCreated;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

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

    private Integer online;

    private Long tenantId;

    private Long proxyId;

    private LocalDateTime latestOnline;

    @Aggregation("count(*)")
    Long totalCount;
}
