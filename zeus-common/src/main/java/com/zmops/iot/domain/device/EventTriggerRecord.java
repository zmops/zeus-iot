package com.zmops.iot.domain.device;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@Data
@Table(name = "event_trigger_record")
@Entity
public class EventTriggerRecord {
    @Id
    private Long id;

    private LocalDateTime createTime;

    private String eventName;

    private String eventValue;

    private String deviceId;

    private Long tenantId;

    private String key;

}
