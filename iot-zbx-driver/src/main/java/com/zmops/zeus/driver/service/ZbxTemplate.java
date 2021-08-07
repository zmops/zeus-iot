package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;

import java.util.Map;

/**
 * @author nantian created at 2021/8/3 15:47
 */

@BaseRequest(baseURL = "${zbxApiUrl}")
public interface ZbxTemplate {

    /**
     * 创建模板
     *
     * @param templateName 模板名称
     * @param groupId      模板分组ID
     * @return
     */
    @Post
    @JsonPath("/template/template.create")
    String templateCreate(@ParamName("templateName") String templateName,
                          @ParamName("groupId") String groupId);


    /**
     * 删除模板
     *
     * @param templateid 模板ID
     * @return
     */
    @Post
    @JsonPath("/template/template.delete")
    String templateDelete(@ParamName("templateid") String templateid);


    /**
     * 更新模板标签
     *
     * @param tagMap 标签Map
     * @return
     */
    @Post
    @JsonPath("/template/template.tag.update")
    String templateTagUpdate(@ParamName("templateId") Integer templateId,
                             @ParamName("tagMap") Map<String, String> tagMap);

}
