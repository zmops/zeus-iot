package com.zmops.iot.domain.sys;

import com.zmops.iot.constant.IdTypeConsts;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author nantian created at 2021/8/1 22:04
 */

@Data
@Entity
@Table(name = "sys_operation_log")
public class SysOperationLog {

    @Id
    @GeneratedValue(generator = IdTypeConsts.ID_SNOW)
    private Long operationLogId;

    private String logType;

    private String logName;

    private Long userId;

    private String className;

    private String method;

    private String succeed;

    private String message;

    private LocalDateTime createTime;

    private Long tenantId;
}
