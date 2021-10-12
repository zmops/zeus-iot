package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

/**
 * @author nantian created at 2021/8/7 23:27
 */

@BaseRequest(
        baseURL = "http://${zbxServerIp}:${zbxServerPort}/zabbix/api_jsonrpc.php",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxAction {


    /**
     * 创建默认 在线状态 Action
     *
     * @param userAuth api token
     * @param scriptId 脚本ID
     * @param groupId  全局主机组ID
     * @return String
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/action/action.init.create")
    String createOfflineStatusAction(@ParamName("userAuth") String userAuth,
                                     @ParamName("name") String name,
                                     @ParamName("tagName") String tagName,
                                     @ParamName("scriptId") String scriptId,
                                     @ParamName("groupId") String groupId);

    /**
     * 创建默认 告警通知 Action
     *
     * @param userAuth api token
     * @param scriptId 脚本ID
     * @param groupId  全局主机组ID
     * @return String
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/action/action.create")
    String createAlarmAction(@ParamName("userAuth") String userAuth,
                                     @ParamName("name") String name,
                                     @ParamName("tagName") String tagName,
                                     @ParamName("scriptId") String scriptId,
                                     @ParamName("groupId") String groupId);

    /**
     * 获取 在线状态 Action
     *
     * @param userAuth api token
     * @return String
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/action/action.offline.get")
    String getOfflineStatusAction(@ParamName("userAuth") String userAuth, @ParamName("name") String name);
}
