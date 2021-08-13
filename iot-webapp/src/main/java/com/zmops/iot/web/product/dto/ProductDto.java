package com.zmops.iot.web.product.dto;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ProductDto {

    private Long productId;

    private Long groupId;

    @JsonProperty("prodName")
    private String name;

    @JsonProperty("prodType")
    private String type;

    private String manufacturer;

    private String model;

    private String remark;

    @JsonProperty("prodCode")
    private String productCode;

    private Integer zbxId;

    private String groupName;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;
    private String prodTypeName;

//    private List<ProductTag.Tag> productTag;
//    private JSONArray            valueMaps;

}
