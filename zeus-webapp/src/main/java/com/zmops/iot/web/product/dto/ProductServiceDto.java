package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProductServiceDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long id;

    @NotBlank(groups = BaseEntity.Create.class)
    private String name;

    private String mark;

    private String remark;

    @NotNull(groups = BaseEntity.Create.class)
    private String relationId;

    @CachedValue(value = "EXECUTE_TYPE", fieldName = "asyncName")
    private String async;

    @CachedValue(value = "WHETHER", fieldName = "inheritName")
    private String inherit;

    private List<ProductServiceParam> productServiceParamList;

    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> ids;
}
