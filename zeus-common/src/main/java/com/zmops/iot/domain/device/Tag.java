package com.zmops.iot.domain.device;

import com.zmops.iot.domain.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tag")
@Entity
public class Tag extends BaseEntity {

    @Id
    private Long id;

    private String sid;

    private String tag;

    private String value;

    private Long templateId;
}
