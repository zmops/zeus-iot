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
@Table(name = "scenes_trigger_record")
@Entity
public class ScenesTriggerRecord {
    @Id
    private Long id;

    private LocalDateTime createTime;

    private String ruleName;

    private Long ruleId;

    private Long tenantId;

    private String triggerType;

    private Long triggerUser;

}
