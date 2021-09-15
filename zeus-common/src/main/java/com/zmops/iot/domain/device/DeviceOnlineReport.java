package com.zmops.iot.domain.device;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode
@Data
@Table(name = "device_online_report")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceOnlineReport {

    @Id
    private Long id;

    private String createTime;

    private Long online;

    private Long offline;

    private String type;

}
