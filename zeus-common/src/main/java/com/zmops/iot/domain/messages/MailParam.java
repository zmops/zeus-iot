package com.zmops.iot.domain.messages;


import com.zmops.iot.domain.messages.MailSetting;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yefei
 **/
@Data
public class MailParam {
    @NotNull
    private String  host;
    @NotNull
    private Integer port;
    @NotNull
    private String  account;
    @NotNull
    private String  password;
    @NotNull
    private String  sender;
    private Integer ssl;
    private Integer tls;
    @NotNull(groups = Test.class)
    private String  receiver;
    @NotNull
    private String  severity;
    @NotNull
    private Integer silent;

    public interface Test {
    }


    public MailSetting getSettings() {
        return MailSetting.builder()
                .host(host)
                .password(password)
                .port(port)
                .account(account)
                .sender(sender)
                .ssl(ssl)
                .tls(tls)
                .severity(severity)
                .silent(silent)
                .build();
    }
}
