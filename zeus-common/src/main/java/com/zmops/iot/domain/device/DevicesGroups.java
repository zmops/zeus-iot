package com.zmops.iot.domain.device;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "devices_groups")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevicesGroups {
    @Id
    private Long id;

    private String deviceId;

    private Long deviceGroupId;
}
