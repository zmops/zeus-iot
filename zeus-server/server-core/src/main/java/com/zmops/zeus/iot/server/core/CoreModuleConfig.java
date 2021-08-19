package com.zmops.zeus.iot.server.core;

import com.zmops.zeus.iot.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/8/16 22:29
 */

@Setter
@Getter
public class CoreModuleConfig extends ModuleConfig {


    private String nameSpace;

    private String restHost;
    private int    restPort;
    private String restContextPath;
    private int    restMinThreads            = 1;
    private int    restMaxThreads            = 200;
    private long   restIdleTimeOut           = 30000;
    private int    restAcceptorPriorityDelta = 0;
    private int    restAcceptQueueSize       = 0;


    /**
     * Timeout for cluster internal communication, in seconds.
     */
    private int remoteTimeout = 20;


    /**
     * The number of threads used to prepare metrics data to the storage.
     *
     * @since 8.7.0
     */
    @Setter
    @Getter
    private int prepareThreads = 2;

    /**
     * The maximum size in bytes allowed for request headers.
     * Use -1 to disable it.
     */
    private int httpMaxRequestHeaderSize = 8192;

}
