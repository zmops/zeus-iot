package com.zmops.iot.web.init;

import com.zmops.iot.domain.sys.SysConfig;
import com.zmops.iot.util.ToolUtil;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author nantian created at 2021/8/7 20:25
 */

@Slf4j
@Component
public class DeviceSatusScriptInit implements CommandLineRunner {


    public static final String GLOBAL_HOST_GROUP_CODE     = "ZEUS_HOST_GROUP_ID";
    public static final String GLOBAL_OFFLINE_ACTION_CODE = "ZEUS_OFFLINE_ACTION_ID";
    public static final String GLOBAL_ALARM_ACTION_CODE   = "ZEUS_ALARM_ACTION_ID";
    public static final String GLOBAL_EXEC_ACTION_CODE    = "ZEUS_EXEC_ACTION_ID";
    public static final String GLOBAL_ADMIN_ROLE_CODE     = "ZEUS_ADMIN_ROLE_ID";
    public static final String GLOBAL_EVENT_ACTION_CODE   = "ZEUS_EVENT_ACTION_ID";

    public static final String SCRIPT_OFFLINE = "__offline_status__";
    public static final String SCRIPT_ALARM   = "__trigger_webhook__";
    public static final String SCRIPT_EXECUTE = "__trigger_execute__";
    public static final String SCRIPT_EVENT   = "__attr_event__";

    public static final String ACTION_TAG_OFFLINE = "__offline__";
    public static final String ACTION_TAG_ALARM   = "__alarm__";
    public static final String ACTION_TAG_EXECUTE = "__execute__";
    public static final String ACTION_TAG_EXENT   = "__event__";


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

        //判断系统参数是否存在 zbx管理员ID
        String roleId = basicSettingsInit.getAdminRoleId();
        DB.update(SysConfig.class).where().eq("code", GLOBAL_ADMIN_ROLE_CODE).asUpdate().set("value", roleId).update();

        //判断只读用户是否存在 不存在就创建一个
        String userId = basicSettingsInit.getCookieUser();
        if (userId == null) {
            String userGroupId = basicSettingsInit.getCookieUserGroup();
            if (userGroupId == null) {
                userGroupId = basicSettingsInit.createCookieUserGroup(groupId);
            }
            String guestRoleId = basicSettingsInit.getGuestRoleId();
            basicSettingsInit.createCookieUser(userGroupId, guestRoleId);
        }


        // 第二步：创建全局回调脚本
        Map<String, String> script = basicSettingsInit.getOfflineStatusScript();
        if (ToolUtil.isEmpty(script)) {
            script = basicSettingsInit.createOfflineStatusScript();
        }

        String offLineActionId = basicSettingsInit.getAction(SCRIPT_OFFLINE);
        if (offLineActionId == null) {
            offLineActionId = basicSettingsInit.createAction(SCRIPT_OFFLINE, ACTION_TAG_OFFLINE, script.get(SCRIPT_OFFLINE), groupId);
        }
        DB.update(SysConfig.class).where().eq("code", GLOBAL_OFFLINE_ACTION_CODE).asUpdate().set("value", offLineActionId).update();

        String alarmActionId = basicSettingsInit.getAction(SCRIPT_ALARM);
        if (alarmActionId == null) {
            alarmActionId = basicSettingsInit.createAction(SCRIPT_ALARM, ACTION_TAG_ALARM, script.get(SCRIPT_ALARM), groupId);
        }
        DB.update(SysConfig.class).where().eq("code", GLOBAL_ALARM_ACTION_CODE).asUpdate().set("value", alarmActionId).update();

        String execActionId = basicSettingsInit.getAction(SCRIPT_EXECUTE);
        if (execActionId == null) {
            execActionId = basicSettingsInit.createAction(SCRIPT_EXECUTE, ACTION_TAG_EXECUTE, script.get(SCRIPT_EXECUTE), groupId);
        }
        DB.update(SysConfig.class).where().eq("code", GLOBAL_EXEC_ACTION_CODE).asUpdate().set("value", execActionId).update();

        String eventActionId = basicSettingsInit.getAction(SCRIPT_EVENT);
        if (eventActionId == null) {
            eventActionId = basicSettingsInit.createAction(SCRIPT_EVENT, ACTION_TAG_EXENT, script.get(SCRIPT_EVENT), groupId);
        }
        DB.update(SysConfig.class).where().eq("code", GLOBAL_EVENT_ACTION_CODE).asUpdate().set("value", eventActionId).update();

        log.info("全局主机组ID：{}，在线触发动作ID：{}，告警触发动作ID：{}，命令执行触发动作ID：{},事件触发动作ID：{}", groupId, offLineActionId, alarmActionId, execActionId, eventActionId);
    }
}
