package com.zmops.zeus.iot.server.receiver.http.provider;

import com.zmops.zeus.iot.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/8/14 22:47
 */

@Getter
@Setter
public class HttpReceiverConfig extends ModuleConfig {


    /**
     * Export http port
     */
    private int port = 8080;

    /**
     * Export http host ip
     */
    private String host = "0.0.0.0";
}
