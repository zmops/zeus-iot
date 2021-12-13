package com.zmops.iot.domain.protocol;

import com.zmops.iot.domain.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "protocol_gateway_mqtt")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolGatewayMqtt {

    private Long protocolGatewayId;

    private Long protocolComponentId;

    private String topic;

}
