package com.zmops.iot.domain.protocol;

import com.zmops.iot.domain.BaseEntity;
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
@Entity
@Table(name = "protocol_component")
public class ProtocolComponent extends BaseEntity {

    @Id
    private Long protocolComponentId;

    private String name;
    private String effectProxy;
    private String status;
    private String remark;
    private Long   tenantId;
    private String fileName;
}
