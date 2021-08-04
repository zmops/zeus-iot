package com.zmops.iot.web.product.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author nantian created at 2021/8/4 15:11
 */

@Data
public class ValueMap {

    @NotNull
    private Long productId;

    @NotNull
    private String valueMapName;

    @NotNull
    private Map<String, String> valueMaps;
}
