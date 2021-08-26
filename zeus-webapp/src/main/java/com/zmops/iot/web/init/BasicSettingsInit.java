package com.zmops.iot.web.init;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.config.ForestConfiguration;
import com.zmops.zeus.driver.service.ZbxAction;
import com.zmops.zeus.driver.service.ZbxHostGroup;
import com.zmops.zeus.driver.service.ZbxInitService;
import com.zmops.zeus.driver.service.ZbxScript;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;
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
    private ZbxScript zbxScript;

    @Autowired
    private ZbxAction zbxAction;

    @Autowired
    private ZbxInitService zbxInitService;

    private String zeusServerIp;
    private String zeusServerPort;
    private String zbxApiToken;


    @PostConstruct
    public void init() {
        zeusServerIp = configuration.getVariables().get("zeusServerIp").toString();
        zeusServerPort = configuration.getVariables().get("zeusServerPort").toString();
        zbxApiToken = configuration.getVariables().get("zbxApiToken").toString();
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
     * 创建 只读权限用户组
     *
     * @param globalHostGroupId 全局主机组ID
     * @return String
     */
    public String createCookieUserGroup(String globalHostGroupId) {
        String response = zbxInitService.createCookieUserGroup(globalHostGroupId);
        return JSON.parseObject(response, ZbxResponseIds.class).getUsrgrpids()[0];
    }

    /**
     * 创建全局主机组
     *
     * @return String
     */
    public String getGlobalHostGroup() {
        String                    response = zbxHostGroup.getGlobalHostGroup(zbxApiToken);
        List<Map<String, String>> ids      = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            return ids.get(0).get("groupid");
        }
        return null;
    }


    public String createOfflineStatusScript() {
        String response = zbxScript.createOfflineStatusScript(zbxApiToken, zeusServerIp, zeusServerPort);
        return JSON.parseObject(response, ZbxResponseIds.class).getScriptids()[0];
    }

    public String getOfflineStatusScript() {
        String                    response = zbxScript.getOfflineStatusScript(zbxApiToken);
        List<Map<String, String>> ids      = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            return ids.get(0).get("scriptid");
        }
        return null;
    }


    public String createOfflineStatusAction(String scriptId, String groupId) {
        String response = zbxAction.createOfflineStatusAction(zbxApiToken, scriptId, groupId);
        return JSON.parseObject(response, ZbxResponseIds.class).getActionids()[0];
    }


    public String getOfflineStatusAction() {
        String                    response = zbxAction.getOfflineStatusAction(zbxApiToken);
        List<Map<String, String>> ids      = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            return ids.get(0).get("actionid");
        }
        return null;
    }


    @Data
    static class ZbxResponseIds {
        String[] groupids;
        String[] scriptids;
        String[] actionids;

        String[] usrgrpids;
    }
}
