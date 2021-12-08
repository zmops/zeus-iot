package com.zmops.iot.web.product.dto.param;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ProductTypeParam {

    @NotNull(groups = BaseEntity.Update.class)
    private Long id;

    private Long pid = 0L;

    private String pids;

    @NotBlank(groups = BaseEntity.Create.class)
    private String name;

    private String remark;

    private Long tenantId;

    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> ids;
}
