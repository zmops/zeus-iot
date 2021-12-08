package com.zmops.iot.web.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class ProductAttrDto {

    private Long attrId;

    @JsonProperty("attrName")
    private String name;

    private String key;

    @CachedValue(value = "UNITS", fieldName = "unitsName")
    private String units;

    @CachedValue(value = "ATTR_TYPE", fieldName = "sourceName")
    private String source;

    private String remark;

    private Long depAttrId;

    @CachedValue(value = "EVENT_LEVEL", fieldName = "eventLevelName")
    private String eventLevel;

    @JsonIgnore
    private String zbxId;

    private String productId;

    @CachedValue(value = "DATA_TYPE", fieldName = "valueTypeName")
    private String valueType;

    private String valuemapid;

    private List<ProductTag.Tag> tags;

    //预处理
    private List<ProductAttr.ProcessingStep> processStepList;

    private Long templateId;

    LocalDateTime createTime;
    LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    private Long createUser;
    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    private Long updateUser;

    private String clock;

    private String value;
    private String originalValue;

    private Integer delay;
    private String delayName;
    //取数间隔单位
    private String unit;

    private String error;
}
