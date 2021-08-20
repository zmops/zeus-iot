package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.domain.BaseDto;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProductDto implements BaseDto {


    private Long productId;

    @CachedValue(type = DicType.ProdType)
    private Long groupId;

    @JsonProperty("prodName")
    private String name;

    @JsonProperty("prodType")
    @CachedValue(value = "DEVICE_TYPE")
    private String type;

    private String manufacturer;

    private String model;

    private String remark;

    @JsonProperty("prodCode")
    private String productCode;

    private String zbxId;

    @CachedValue(type = DicType.SysUserName)
    private String createUser;
    private String createTime;
    @CachedValue(type = DicType.SysUserName)
    private String updateUser;
    private String updateTime;

    private Long deviceNum;

//    private List<ProductTag.Tag> productTag;
//    private JSONArray            valueMaps;

}
