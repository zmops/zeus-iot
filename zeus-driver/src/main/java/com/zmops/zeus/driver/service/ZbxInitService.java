package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.callback.OnSaveCookie;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

/**
 * @author nantian created at 2021/8/26 12:20
 */

public interface ZbxInitService {

    @Post(
            url = "http://${zbxServerIp}/zabbix/index.php",
            headers = {"Content-Type: application/x-www-form-urlencoded"}
    )
    String getCookie(@Body String body, OnSaveCookie onSaveCookie);


    @Post(
            url = "${zbxApiUrl}",
            interceptor = JsonBodyBuildInterceptor.class
    )
    @JsonPath("/usergroup/cookieUserGroupCreate")
    String createCookieUserGroup(String hostGroupId);
}
