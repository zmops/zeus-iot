package com.zmops.iot.domain.proxy;

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
@Table(name = "proxy")
public class Proxy extends BaseEntity {

    @Id
    private Long id;

    private String  name;
    private String  mode      = "1";
    private String  address;
    private String  remark;
    private Integer tlsAccept = 1;
    private String  zbxId;
    private Long    tenantId;
}
