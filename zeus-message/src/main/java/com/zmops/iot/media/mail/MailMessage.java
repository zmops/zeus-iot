package com.zmops.iot.media.mail;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  邮件内容
 * @author yefei
 *
 **/
@Data
@NoArgsConstructor
public class MailMessage {
    /**
     * 收件人名称
     */
    private String user;
    /**
     * 告警信息
     */
    private String message;
    /**
     * 服务
     */
    private String server;
    /**
     * 对象类型
     */
    private String objectType;
    /**
     * 告警规则
     */
    private String alarmRule;
    /**
     * 告警等级
     */
    private String level;

    /**
     * 告警时间
     */
    private String time;

    /**
     * ip
     */
    private String ip;

    /**
     * 指标值
     */
    private String table;
}
