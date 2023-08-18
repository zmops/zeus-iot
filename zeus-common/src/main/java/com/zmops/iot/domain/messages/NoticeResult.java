package com.zmops.iot.domain.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yefei
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResult {
    private NoticeStatus status;
    private String       msg;

    /**
     * 告警信息
     */
    private String alarmInfo;
    /**
     * 接收账号
     */
    private String receiveAccount;


    public static NoticeResult skipped() {
        return NoticeResult.builder()
                .status(NoticeStatus.skipped).build();
    }

    public static NoticeResult success() {
        return NoticeResult.builder()
                .status(NoticeStatus.success).build();
    }

    public static NoticeResult success(String alarmInfo, String receiveAccount) {
        return NoticeResult.builder()
                .status(NoticeStatus.success).
                        alarmInfo(alarmInfo).
                        receiveAccount(receiveAccount).build();
    }

    public static NoticeResult failed(String error) {
        return NoticeResult.builder()
                .status(NoticeStatus.failed)
                .msg(error).build();
    }

    public static NoticeResult failed(String error, String alarmInfo, String receiveAccount) {
        return NoticeResult.builder()
                .status(NoticeStatus.failed).
                        alarmInfo(alarmInfo).
                        receiveAccount(receiveAccount)
                .msg(error).build();
    }

    public enum NoticeStatus {
        skipped, success, failed;
    }
}
