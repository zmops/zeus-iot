package com.zmops.zeus.iot.server.transfer.provider;

import com.zmops.zeus.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/9/22 16:46
 * <p>
 * ndjson 文件读取配置
 */
@Getter
@Setter
public class ServerTransferConfig extends ModuleConfig {

    private String name;

    private String pattern;

    // 文件读取超时 线程回收
    private Integer fileMaxWait;

}
