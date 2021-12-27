package com.zmops.zeus.iot.web.domain;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class ProtocolService {

    private Long id;

    private String name;

    private String protocolType;
    private String remark;

    private String  url;
    private String  ip;
    private Integer port;
    private Integer msgLength;
    private String  clientId;
}
