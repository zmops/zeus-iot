package com.zmops.zeus.iot.server.h2.provider;

import com.zmops.zeus.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/10/24 16:45
 * <p>
 * H2 配置，用于 IoT Server 本地组件状态缓存
 */

@Getter
@Setter
public class LocalH2Config extends ModuleConfig {

    private String driver = "org.h2.jdbcx.JdbcDataSource";
    private String url = "jdbc:h2:~/zeus_iot_db;DB_CLOSE_DELAY=-1";
    private String user = "sa";
    private String password = "sa";

    /**
     * 模块版本号
     */
    private String version;
}
