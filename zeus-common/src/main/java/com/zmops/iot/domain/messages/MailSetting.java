package com.zmops.iot.domain.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mail_setting")
public class MailSetting {

    @Id
    private Integer id;
    private String  host;
    private Integer port;
    private String  account;
    private String  password;
    private String  sender;
    private Integer ssl;
    private Integer tls;
    private String  severity;
    private Integer silent;
    private Long    tenantId;


    public boolean sslAvailable() {
        return ssl != null && ssl == 1;
    }

    public boolean tlsAvailable() {
        return tls != null && tls == 1;
    }
}
