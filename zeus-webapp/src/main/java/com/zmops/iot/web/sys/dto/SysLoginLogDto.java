package com.zmops.iot.web.sys.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * @author nantian created at 2021/7/31 18:20
 */
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class SysLoginLogDto {

    long loginLogId;

    String logName;

    @CachedValue(type = DicType.SysUserName)
    Long userId;

    LocalDateTime createTime;

    String succeed;

    String message;

    String ipAddress;
}
