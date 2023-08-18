package com.zmops.iot.web.task.dto;

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
public class TaskDto {

    private Integer id;

    private String scheduleType = "CRON";

    private String scheduleConf;

    private String misfireStrategy = "DO_NOTHING";

    private Integer taskTimeout = 0;

    private Integer taskFailRetryCount = 0;

    @CachedValue(value = "STATUS", fieldName = "triggerStatusName")
    private String triggerStatus = "ENABLE";

    private Long triggerLastTime = 0L;

    private Long triggerNextTime = 0L;

    private String remark;

    private String executorParam;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName, fieldName = "createUserName")
    private Long createUser;

    @CachedValue(type = DicType.SysUserName, fieldName = "updateUserName")
    private Long updateUser;
}
