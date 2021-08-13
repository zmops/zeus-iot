package com.zmops.iot.domain.sys;

import com.zmops.iot.constant.IdTypeConsts;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;


/**
 * @author nantian created at 2021/7/31 18:20
 */
@Data
@Table(name = "sys_login_log")
@Entity
public class SysLoginLog {

    @Id
    @GeneratedValue(generator = IdTypeConsts.ID_SNOW)
    long loginLogId;

    String logName;

    Long userId;

    LocalDateTime createTime;

    String succeed;

    String message;

    String ipAddress;
}
