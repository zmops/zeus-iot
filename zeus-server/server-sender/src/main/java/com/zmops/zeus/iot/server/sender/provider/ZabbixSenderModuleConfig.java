package com.zmops.zeus.iot.server.sender.provider;

import com.zmops.zeus.iot.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/8/14 14:41
 */

@Setter
@Getter
public class ZabbixSenderModuleConfig extends ModuleConfig {

    /**
     * Export tcp port
     */
    private int port = 10051;

    /**
     * Bind to host
     */
    private String host = "127.0.0.1";
}
