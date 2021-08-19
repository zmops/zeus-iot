package com.zmops.zeus.iot.server.core;

import com.zmops.zeus.iot.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/8/16 22:29
 */

@Setter
@Getter
public class CoreModuleConfig extends ModuleConfig {

    private int prepareThreads = 2;
}
