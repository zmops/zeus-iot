package com.zmops.iot.web.init;

import com.zmops.iot.domain.sys.SysConfig;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

        //判断只读用户是否存在 不存在就创建一个
        String userId = basicSettingsInit.getCookieUser();
        if (userId == null) {
            String userGroupId = basicSettingsInit.getCookieUserGroup();
            if (userGroupId == null) {
                userGroupId = basicSettingsInit.createCookieUserGroup(groupId);
            }
            basicSettingsInit.createCookieUser(userGroupId);
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
