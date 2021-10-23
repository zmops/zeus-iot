package com.zmops.zeus.iot.server.receiver.provider;

import com.zmops.zeus.iot.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/10/23 21:25
 */

@Getter
@Setter
public class CamelReceiverConfig extends ModuleConfig {

    private String version;
}
