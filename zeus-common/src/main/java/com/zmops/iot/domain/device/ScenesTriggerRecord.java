package com.zmops.iot.domain.device;

import lombok.Data;

import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@Data
public class ScenesTriggerRecord {
    @Id
    private Long id;

    private LocalDateTime createTime;

    private String ruleName;

    private Long ruleId;

    private Long tenantId;
}
