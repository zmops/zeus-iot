package com.zmops.iot.web.sys.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.constant.IdTypeConsts;
import com.zmops.iot.domain.BaseDto;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 字典类型表
 * </p>
 */
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class SysDictTypeDto implements BaseDto {

    /**
     * 字典类型id
     */
    private Long dictTypeId;

    /**
     * 字典类型编码
     */
    private String code;

    /**
     * 字典类型名称
     */
    private String name;

    /**
     * 字典描述
     */
    private String remark;

    /**
     * 是否是系统字典，Y-是，N-否
     */
    private String systemFlag = "N";

    /**
     * 状态
     */
    @CachedValue(value = "STATUS")
    private String status;

    /**
     * 排序
     */
    private Integer sort;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    Long createUser;

    Long updateUser;

}
