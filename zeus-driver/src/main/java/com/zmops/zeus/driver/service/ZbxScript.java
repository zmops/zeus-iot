package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

/**
 * @author nantian created at 2021/8/7 20:42
 */
@BaseRequest(
        baseURL = "${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxScript {


    /**
     * 创建 在线状态 回调地址
     *
     * @param userApiToken   apiToekn
     * @param zeusServerIp   平台IP
     * @param zeusServerPort 平台端口
     * @return String
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/script/script.init.create")
    String createOfflineStatusScript(@ParamName("userAuth") String userApiToken,
                                     @ParamName("zeusServerIp") String zeusServerIp,
                                     @ParamName("zeusServerPort") String zeusServerPort);


    /**
     * 获取 在线状态 回调脚本 ID
     *
     * @param userAuth 授权Token
     * @return String
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/script/script.offline.get")
    String getOfflineStatusScript(@ParamName("userAuth") String userAuth);
}
