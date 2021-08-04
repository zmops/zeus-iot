package com.zmops.iot.web.product.dto;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author nantian created at 2021/8/3 19:46
 */
@Data
public class ProductBasicInfo {

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Delete.class})
    private Long productId;

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private Long groupId; //产品分类ID

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    @Pattern(regexp = "^[A-Za-z0-9]+$",
            message = "产品编号不能包含中文字符")
    private String prodCode; //产品编号

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String prodName; //产品名称

    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String prodType; //产品类型

    private String manufacturer; //产品厂商

    private String model; //产品型号

    private String remark; //备注
}
