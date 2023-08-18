package com.zmops.zeus.iot.web.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yefei
 **/
@Data
public class ProtocolGateway {

    private Long id;

    private String name;
    private Long   protocolServiceId;
    private Long   protocolComponentId;
    private String status;
    private String remark;
}
