package com.zmops.iot.domain.device;

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
@Table(name = "service_execute_record")
@Entity
public class ServiceExecuteRecord {

    @Id
    private Long id;

    private LocalDateTime createTime;

    private String serviceName;

    private String deviceId;

    private String param;

    private Long tenantId;

    private String executeType;

    private Long executeUser;

    private Long executeRuleId;

}
