package com.zmops.iot.domain.messages;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author yefei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notice_record")
public class NoticeRecord {
    @Id
    private Integer       recordId;
    private Long          userId;
    private String       problemId;
    private Integer       noticeType;
    private String        noticeStatus;
    private String        noticeMsg;
    private LocalDateTime creatTime;
    private String        alarmInfo;
    private String        receiveAccount;
}
