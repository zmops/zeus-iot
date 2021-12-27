package com.zmops.zeus.iot.web.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
public class ProtocolComponent {

    private Long id;

    private String name;

    private String uniqueId;

    private String status;

    private String remark;

    private String fileName;
}
