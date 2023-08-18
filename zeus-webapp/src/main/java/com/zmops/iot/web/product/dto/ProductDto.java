package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProductDto {


    private Long productId;

    @CachedValue(type = DicType.ProdType, fieldName = "groupName")
    private Long groupId;

    @JsonProperty("prodName")
    private String name;

    @JsonProperty("prodType")
    @CachedValue(value = "DEVICE_TYPE", fieldName = "typeName")
    private String type;

    private String manufacturer;

    private String model;

    private String remark;

    @JsonProperty("prodCode")
    private String productCode;

    private String zbxId;


    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    private Long          createUser;
    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    private Long          updateUser;

    private Long deviceNum;

    @CachedValue(type = DicType.Tenant, fieldName = "tenantName")
    private Long tenantId;

    private String icon;

}
