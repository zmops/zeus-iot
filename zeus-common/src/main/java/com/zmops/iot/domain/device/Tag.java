package com.zmops.iot.domain.device;

import com.zmops.iot.constant.IdTypeConsts;
import com.zmops.iot.domain.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.PreparedStatement;

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
