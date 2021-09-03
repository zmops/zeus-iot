package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.domain.BaseDto;
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
public class ProductServiceDto implements BaseDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long id;

    @NotBlank(groups = BaseEntity.Create.class)
    private String name;

    private String mark;

    private String remark;

    @NotNull(groups = BaseEntity.Create.class)
    private String sid;

    @CachedValue(value = "EXECUTE_TYPE")
    private String async;

    private Long templateId;

    private List<ProductServiceParam> productServiceParamList;


    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> ids;
}
