package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine;

import com.zmops.zeus.iot.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/9/3 23:40
 */
@Setter
@Getter
public class TDEngineStorageConfig extends ModuleConfig {

    private final String driver = "com.taosdata.jdbc.TSDBDriver";

    private String url = "";
    private String user = "";
    private String password = "";

    private int dataKeep = 365; //数据保留天数
    private int oneFileDays = 10; // 每多少天一个数据文件
    private int memoryBlocks = 6; // 内存块数
    private int dataUpdate = 1; // 是否允许更新数据，1 允许
}
