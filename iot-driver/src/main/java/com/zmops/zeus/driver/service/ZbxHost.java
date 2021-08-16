package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import lombok.Data;

import java.util.List;

/**
 * @author nantian created at 2021/8/3 14:54
 * <p>
 * 主机驱动
 */
@BaseRequest(baseURL = "${zbxApiUrl}")
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
     * @param hostid   主机ID
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




    @Data
    class Macro {
        String key;
        String value;
        String desc;
    }
}
