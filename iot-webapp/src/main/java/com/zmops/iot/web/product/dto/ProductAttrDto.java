package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.domain.BaseDto;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nantian created at 2021/8/5 13:53
 * <p>
 * dto for product attribute
 */

@Data
@JsonSerialize(using = CachedValueFilter.class)
public class ProductAttrDto implements BaseDto {

    private Long attrId;

    @JsonProperty("attrName")
    private String name;

    private String key;

    @CachedValue(value = "UNITS")
    private String units;

    @CachedValue(value = "DEVICE_TYPE")
    private String source;

    private String remark;

    private Long depAttrId;

    @JsonIgnore
    private String zbxId;

    private Long productId;

    @CachedValue(value = "DATA_TYPE")
    private String valueType;

    private String valuemapid;

    private List<ProductTag.Tag> tags;

    //预处理
    private List<ProductAttr.ProcessingStep> processStepList;

    private Long templateId;

    LocalDateTime createTime;
    LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName)
    private Long createUser;
    @CachedValue(type = DicType.SysUserName)
    private Long updateUser;

}
