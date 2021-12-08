package com.zmops.iot.web.sys.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author nantian created at 2021/8/1 22:04
 */

@Data
@JsonSerialize(using = CachedValueFilter.class)
public class SysOperationLogDto {

    private Long operationLogId;

    private String logType;

    private String logName;

    @CachedValue(type = DicType.SysUserName, fieldName = "userName")
    private Long userId;

    private String className;

    private String method;

    private String succeed;

    private String message;

    private LocalDateTime createTime;

    private Long tenantId;
}
