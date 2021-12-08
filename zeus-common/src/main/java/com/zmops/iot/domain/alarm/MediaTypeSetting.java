package com.zmops.iot.domain.alarm;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "media_type_setting")
@Entity
public class MediaTypeSetting {
    @Id
    private Integer id;

    private String type;

    private String template;

    private String webhooks;

    private Long tenantId;
}
