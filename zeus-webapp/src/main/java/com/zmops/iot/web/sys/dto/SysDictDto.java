package com.zmops.iot.web.sys.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 基础字典
 * </p>
 */
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class SysDictDto {

    /**
     * 字典id
     */
    private Long dictId;

    /**
     * 所属字典类型的id
     */
    private Long dictTypeId;

    /**
     * 字典编码
     */
    private String code;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 状态
     */
    @CachedValue(value = "STATUS", fieldName = "statusName")
    private String status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 字典的描述
     */
    private String remark;

    private String groups;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    Long createUser;

    Long updateUser;
}
