package com.zmops.iot.domain.alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alarm message represents the details of each alarm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMessage {
    private int scopeId;
    private String scope;
    private String name;
    private String id0;
    private String id1;
    private String ruleName;
    private String alarmMessage;
    private long startTime;
    private transient int period;
    private transient boolean onlyAsCondition;
}
