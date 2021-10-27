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
    public static final String GLOBAL_ADMIN_ROLE_CODE = "ZEUS_ADMIN_ROLE_ID";

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

    }
}
