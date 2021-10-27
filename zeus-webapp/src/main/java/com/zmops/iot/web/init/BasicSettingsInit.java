package com.zmops.iot.web.init;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.config.ForestConfiguration;
import com.zmops.zeus.driver.service.ZbxHostGroup;
import com.zmops.zeus.driver.service.ZbxInitService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/8/7 22:52
 */

@Service
public class BasicSettingsInit {

    @Autowired
    private ForestConfiguration configuration;

    @Autowired
    private ZbxHostGroup zbxHostGroup;

    @Autowired
    private ZbxInitService zbxInitService;

    private String zbxApiToken;

    @PostConstruct
    public void init() {
        zbxApiToken = configuration.getVariableValue("zbxApiToken").toString();
    }

    /**
     * 查询全局主机组
     *
     * @return String
     */
    public String getGlobalHostGroup() {
        String response = zbxHostGroup.getGlobalHostGroup(zbxApiToken);
        List<Map<String, String>> ids = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            return ids.get(0).get("groupid");
        }
        return null;
    }

    /**
     * 创建默认全局主机组
     *
     * @return Integer
     */
    public String createGlobalHostGroup() {
        String response = zbxHostGroup.createGlobalHostGroup(zbxApiToken);
        return JSON.parseObject(response, ZbxResponseIds.class).getGroupids()[0];
    }

    /**
     * 查询只读权限用户组
     *
     * @return String
     */
    public String getCookieUserGroup() {
        String response = zbxInitService.getCookieUserGroup(zbxApiToken);
        List<Map<String, String>> ids = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            return ids.get(0).get("usrgrpid");
        }
        return null;
    }

    /**
     * 创建 只读权限用户组
     *
     * @param globalHostGroupId 全局主机组ID
     * @return String
     */
    public String createCookieUserGroup(String globalHostGroupId) {
        String response = zbxInitService.createCookieUserGroup(globalHostGroupId, zbxApiToken);
        return JSON.parseObject(response, ZbxResponseIds.class).getUsrgrpids()[0];
    }

    /**
     * 查询 只读权限用户
     *
     * @return String
     */
    public String getCookieUser() {
        String response = zbxInitService.getCookieUser(zbxApiToken);
        List<Map<String, String>> ids = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            return ids.get(0).get("userid");
        }
        return null;
    }

    /**
     * 创建 只读权限用户
     *
     * @param groupId 只读用户组ID
     * @return String
     */
    public String createCookieUser(String groupId, String roleId) {
        String response = zbxInitService.createCookieUser(groupId, zbxApiToken, roleId);
        return JSON.parseObject(response, ZbxResponseIds.class).getUserids()[0];
    }

    /**
     * 查询 管理员用户角色
     *
     * @return String
     */
    public String getAdminRoleId() {
        String response = zbxInitService.getAdminRole(zbxApiToken);
        List<Map<String, String>> ids = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            return ids.get(0).get("roleid");
        }
        return null;
    }

    /**
     * 查询 访客用户角色
     *
     * @return String
     */
    public String getGuestRoleId() {
        String response = zbxInitService.getGuestRole(zbxApiToken);
        List<Map<String, String>> ids = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            return ids.get(0).get("roleid");
        }
        return null;
    }


    @Data
    static class ZbxResponseIds {
        String[] groupids;
        String[] userids;
        String[] usrgrpids;
    }
}
