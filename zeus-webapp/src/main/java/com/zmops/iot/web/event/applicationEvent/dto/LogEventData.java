package com.zmops.iot.web.event.applicationEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yefei
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogEventData {

    private Long eventRuleId;
    private String relationId;
    private String triggerType;
    private Long triggerUser;

}
