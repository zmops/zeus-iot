package com.zmops.iot.web.product.dto;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author nantian created at 2021/8/4 15:11
 */

@Data
public class ValueMap {

    @NotNull(groups = BaseEntity.Create.class)
    private Long productId;

    @NotNull(groups = BaseEntity.Create.class)
    private String valueMapName;

    @NotNull(groups = BaseEntity.Create.class)
    private Map<String, String> valueMaps;


    @NotNull(groups = BaseEntity.Delete.class)
    private String valuemapid;
}
