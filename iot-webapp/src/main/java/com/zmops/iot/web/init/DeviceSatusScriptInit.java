package com.zmops.iot.web.init;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.config.ForestConfiguration;
import com.zmops.iot.domain.sys.SysConfig;
import com.zmops.iot.domain.sys.query.QSysConfig;
import com.zmops.zeus.driver.service.ZbxScript;
import io.ebean.DB;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author nantian created at 2021/8/7 20:25
 */

@Slf4j
@Component
public class DeviceSatusScriptInit implements CommandLineRunner {


    public static final String GLOBAL_HOST_GROUP_CODE = "ZEUS_HOST_GROUP_ID";
    public static final String GLOBAL_ACTION_CODE     = "ZEUS_ACTION_ID";

    @Autowired
    private BasicSettingsInit basicSettingsInit;


    @Override
    public void run(String... args) throws Exception {
        // 第一步：判断系统参数是否存在 全局主机组 ID
        String groupId = basicSettingsInit.getGlobalHostGroup();
        if (groupId == null) {
            groupId = basicSettingsInit.createGlobalHostGroup();
        }

        DB.update(SysConfig.class).where().eq("code", GLOBAL_HOST_GROUP_CODE).asUpdate().set("value", groupId).update();

        // 第二步：创建全局回调脚本
        String scriptId = basicSettingsInit.getOfflineStatusScript();
        if (scriptId == null) {
            scriptId = basicSettingsInit.createOfflineStatusScript();
        }

        String actionId = basicSettingsInit.getOfflineStatusAction();
        if (actionId == null) {
            actionId = basicSettingsInit.createOfflineStatusAction(scriptId, groupId);
        }

        DB.update(SysConfig.class).where().eq("code", GLOBAL_ACTION_CODE).asUpdate().set("value", actionId).update();

        log.info("全局主机组ID：{}，在线触发动作ID：{}", groupId, actionId);
    }
}
