package com.zmops.iot.domain.alarm;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "problem")
@Entity
public class Problem {

    @Id
    private Long eventId;

    private Long objectId;

    private LocalDateTime clock;

    private LocalDateTime rClock;

    private String name;

    private Integer acknowledged;

    private Integer severity;

    private String deviceId;

}
