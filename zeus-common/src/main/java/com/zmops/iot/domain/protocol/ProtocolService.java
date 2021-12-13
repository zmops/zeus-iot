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
@Table(name = "protocol_service")
public class ProtocolService extends BaseEntity {

    @Id
    private Long protocolServiceId;

    private String  name;
    private String  effectProxy;
    private String  protocolType;
    private String  remark;
    private Long    tenantId;
    private String url;
    private String ip;
    private Integer port;
    private Integer msgLength;
    private String clientId;
}
