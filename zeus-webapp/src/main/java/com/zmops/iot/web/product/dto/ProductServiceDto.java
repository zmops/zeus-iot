package com.zmops.iot.web.product.dto;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductServiceParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ProductServiceDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long id;

    @NotBlank(groups = BaseEntity.Create.class)
    private String name;

    private String mark;

    private String remark;

    @NotNull(groups = BaseEntity.Create.class)
    private Long sid;

    private Integer async;

    private Long templateId;

    private List<ProductServiceParam> productServiceParamList;


    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> ids;
}
