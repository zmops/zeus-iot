package com.zmops.iot.domain.protocol;

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
@Entity
@Table(name = "protocol_gateway")
public class ProtocolGateway extends BaseEntity {

    @Id
    private Long protocolGatewayId;

    private String  name;
    private String  protocolType;
    private Long  protocolServiceId;
    private Long  protocolComponentId;
    private String status;
    private String  remark;
    private Long    tenantId;
    private Integer qos;
}
