package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

import java.util.List;

/**
 * @author nantian created at 2021/8/2 13:09
 * <p>
 * ZABBIX User 接口
 */

@BaseRequest(
        baseURL = "${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxUser {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录返回的状态信息
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/user/userLogin")
    String userLogin(@ParamName("username") String username,
                     @ParamName("password") String password);


    /**
     * 用户创建
     *
     * @param name     账号
     * @param password 密码
     * @param usrGrpId 用户组ID
     * @return 用户信息
     */
    @Post
    @JsonPath("/user/userAdd")
    String userAdd(@ParamName("name") String name,
                   @ParamName("password") String password,
                   @ParamName("usrGrpId") String usrGrpId,
                   @ParamName("roleId") String roleId);

    /**
     * 用户修改
     *
     * @param userId   用户id
     * @param usrGrpId 用户组ID
     * @return 用户信息
     */
    @Post
    @JsonPath("/user/userUpdate")
    String userUpdate(@ParamName("userId") String userId,
                      @ParamName("usrGrpId") String usrGrpId,
                      @ParamName("roleId") String roleId);

    /**
     * 用户删除
     *
     * @param usrids 用户id
     * @return 用户信息
     */
    @Post
    @JsonPath("/user/userDelete")
    String userDelete(@ParamName("usrids") List<String> usrids);

    /**
     * 用户修改密码
     *
     * @param userId 用户id
     * @param passwd 用户组ID
     * @return 用户信息
     */
    @Post
    @JsonPath("/user/userUpdatePwd")
    String updatePwd(@ParamName("userId") String userId, @ParamName("passwd") String passwd);

    /**
     * 用户查询
     *
     * @return
     */
    @Post
    @JsonPath("/user/userGet")
    String getUser(@ParamName("userids") String userIds);
}
