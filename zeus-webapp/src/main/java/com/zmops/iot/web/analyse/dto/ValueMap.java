package com.zmops.iot.web.analyse.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ValueMap {
    private String valuemapid;

    private String name;

    private List<Mapping> mappings;
}
