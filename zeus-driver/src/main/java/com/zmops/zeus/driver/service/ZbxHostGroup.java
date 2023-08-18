package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

import java.util.List;

/**
 * @author nantian created at 2021/8/3 16:02
 */

@BaseRequest(
        baseURL = "http://${zbxServerIp}:${zbxServerPort}${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxHostGroup {


    /**
     * 获取 全局 主机组
     *
     * @param userAuth api token
     * @return String
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/hostgroup/hostgroup.global.get")
    String getGlobalHostGroup(@ParamName("userAuth") String userAuth);


    /**
     * 创建默认全局主机组
     *
     * @param userAuth userToken
     * @return String
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/hostgroup/hostgroup.init.create")
    String createGlobalHostGroup(@ParamName("userAuth") String userAuth);

    /**
     * 创建主机组
     *
     * @param hostGroupName 主机组名称
     * @return String
     */
    @Post
    @JsonPath("/hostgroup/hostgroup.create")
    String hostGroupCreate(@ParamName("hostGroupName") String hostGroupName);


    /**
     * 删除主机组
     *
     * @param hostGrpIds 主机组IDS
     * @return String
     */
    @Post
    @JsonPath("/hostgroup/hostgroup.delete")
    String hostGroupDelete(@ParamName("hostGroupIds") List<String> hostGrpIds);

    /**
     * 获取 主机组
     *
     * @return String
     */
    @Post
    @JsonPath("/hostgroup/hostgroup.get")
    String getHostGroup(@ParamName("groupids") String groupids);
}
