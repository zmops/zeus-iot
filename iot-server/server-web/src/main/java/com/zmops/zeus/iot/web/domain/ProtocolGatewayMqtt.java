package com.zmops.zeus.iot.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yefei
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolGatewayMqtt {

    private Long protocolGatewayId;

    private Long protocolComponentId;

    private String topic;

}
