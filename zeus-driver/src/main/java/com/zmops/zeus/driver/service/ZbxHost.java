package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/8/3 14:54
 * <p>
 * 主机驱动
 */
@BaseRequest(
        baseURL = "http://${zbxServerIp}:${zbxServerPort}${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxHost {

    /**
     * 创建主机
     *
     * @param hostName   主机名
     * @param groupids   主机分组IDs
     * @param templateid 主机模板ID，对应产品物模型ID
     * @return
     */
    @Post
    @JsonPath("/host/host.create")
    String hostCreate(@ParamName("hostName") String hostName,
                      @ParamName("groupids") List<String> groupids,
                      @ParamName("templateid") String templateid);

    /**
     * 修改主机
     *
     * @param hostid     主机ID
     * @param groupids   主机分组IDs
     * @param templateid 主机模板ID，对应产品物模型ID
     * @return
     */
    @Post
    @JsonPath("/host/host.update")
    String hostUpdate(@ParamName("hostid") String hostid,
                      @ParamName("groupids") List<String> groupids,
                      @ParamName("templateid") String templateid);
    /**
     * 修改主机状态
     *
     * @param hostid     主机ID
     * @param status   主机状态
     * @return
     */
    @Post
    @JsonPath("/host/host.status.update")
    String hostStatusUpdate(@ParamName("hostid") String hostid,
                      @ParamName("status") String status);

    /**
     * 删除主机
     *
     * @param hostIds 主机ID
     * @return
     */
    @Post
    @JsonPath("/host/host.delete")
    void hostDelete(@ParamName("hostIds") List<String> hostIds);

    /**
     * 查询主机详情
     *
     * @param hostid 主机ID
     */
    @Post
    @JsonPath("/host/host.get")
    String hostDetail(@ParamName("hostid") String hostid);

    /**
     * 查询主机
     *
     * @param host 主机ID
     */
    @Post
    @JsonPath("/host/host.get")
    String hostGet(@ParamName("host") String host);

    /**
     * 更新主机宏
     *
     * @param hostId    主机ID
     * @param macroList macro 列表
     * @return
     */
    @Post
    @JsonPath("/host/host.macro.update")
    String hostMacroUpdate(@ParamName("hostId") String hostId,
                           @ParamName("macros") List<Macro> macroList);


    /**
     * 更新主机标签
     *
     * @param tagMap 标签Map
     * @return String
     */
    @Post
    @JsonPath("/host/host.tag.update")
    String hostTagUpdate(@ParamName("hostId") String hostId,
                         @ParamName("tagMap") Map<String, String> tagMap);


    /**
     * 通过主机名获取 模板IDS
     *
     * @param hostname
     * @return
     */
    @Post
    @JsonPath("/host/host.tempid.get")
    String hostTempidGet(@ParamName("hostname") String hostname);


    @Data
    class Macro {
        String key;
        String value;
        String desc;
    }
}
