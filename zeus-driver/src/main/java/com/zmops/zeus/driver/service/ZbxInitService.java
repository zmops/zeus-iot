package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

/**
 * @author nantian created at 2021/8/26 12:20
 */

public interface ZbxInitService {

    @Post(
            url = "${zbxApiUrl}",
            headers = "authTag: noAuth",
            interceptor = JsonBodyBuildInterceptor.class
    )
    @JsonPath("/usergroup/cookieUserGroupCreate")
    String createCookieUserGroup(@ParamName("hostGroupId") String hostGroupId, @ParamName("userAuth") String userAuth);

    @Post(
            url = "${zbxApiUrl}",
            headers = "authTag: noAuth",
            interceptor = JsonBodyBuildInterceptor.class
    )
    @JsonPath("/usergroup/cookieUserGroupGet")
    String getCookieUserGroup(@ParamName("userAuth") String userAuth);

    @Post(
            url = "${zbxApiUrl}",
            headers = "authTag: noAuth",
            interceptor = JsonBodyBuildInterceptor.class
    )
    @JsonPath("/user/cookieUserGet")
    String getCookieUser(@ParamName("userAuth") String userAuth);

    @Post(
            url = "${zbxApiUrl}",
            headers = "authTag: noAuth",
            interceptor = JsonBodyBuildInterceptor.class
    )
    @JsonPath("/user/cookieUserAdd")
    String createCookieUser(@ParamName("usrGrpId") String usrGrpId,
                            @ParamName("userAuth") String userAuth,
                            @ParamName("roleId") String roleId);

    @Post(
            url = "${zbxApiUrl}",
            headers = "authTag: noAuth",
            interceptor = JsonBodyBuildInterceptor.class
    )
    @JsonPath("/role/adminRole.get")
    String getAdminRole(@ParamName("userAuth") String zbxApiToken);

    @Post(
            url = "${zbxApiUrl}",
            headers = "authTag: noAuth",
            interceptor = JsonBodyBuildInterceptor.class
    )
    @JsonPath("/role/guestRole.get")
    String getGuestRole(@ParamName("userAuth") String zbxApiToken);
}
