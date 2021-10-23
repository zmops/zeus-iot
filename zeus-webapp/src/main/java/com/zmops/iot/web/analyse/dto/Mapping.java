package com.zmops.iot.web.analyse.dto;

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
public class Mapping {
    private Integer mappingid;
    private Integer valuemapid;
    private String value;
    private String newvalue;
}
