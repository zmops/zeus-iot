package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;

import java.util.List;

/**
 * @author nantian created at 2021/8/3 16:02
 */

@BaseRequest(baseURL = "${zbxApiUrl}")
public interface ZbxHostGroup {

    /**
     * 创建主机组
     *
     * @param hostGroupName 主机组名称
     * @return
     */
    @Post
    @JsonPath("/hostgroup/hostgroup.create")
    String hostGroupCreate(@ParamName("hostGroupName") String hostGroupName);


    /**
     * 删除主机组
     *
     * @param hostGrpIds 主机组IDS
     * @return
     */
    @Post
    @JsonPath("/hostgroup/hostgroup.delete")
    String hostGroupDelete(@ParamName("hostgGroupIds") List<Long> hostGrpIds);
}
