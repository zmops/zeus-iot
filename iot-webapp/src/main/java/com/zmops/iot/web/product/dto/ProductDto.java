package com.zmops.iot.web.product.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ProductDto {

    private Long productId;

    private Long groupId;

    private String name;

    private String type;

    private String manufacturer;

    private String model;

    private String remark;

    private String productCode;

    private Integer zbxId;

    private String groupName;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;

    private List<ProductTag.Tag> productTag;


}
