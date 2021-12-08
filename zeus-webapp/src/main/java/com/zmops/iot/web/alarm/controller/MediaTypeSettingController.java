package com.zmops.iot.web.alarm.controller;

import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.domain.alarm.MediaTypeSetting;
import com.zmops.iot.domain.alarm.query.QMediaTypeSetting;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.model.response.SuccessResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.service.MediaTypeSettingService;
import io.ebean.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yefei
 **/
@RestController
@RequestMapping("/media/type")
public class MediaTypeSettingController {

    @Autowired
    MediaTypeSettingService mediaTypeSettingService;


    @GetMapping("/list")
    public ResponseData list(@RequestParam(value = "type", required = false) String type) {
        Long              tenantId          = LoginContextHolder.getContext().getUser().getTenantId();
        QMediaTypeSetting qMediaTypeSetting = new QMediaTypeSetting();
        if (null != tenantId) {
            qMediaTypeSetting.tenantId.eq(tenantId);
        } else {
            qMediaTypeSetting.tenantId.isNull();
        }
        if (ToolUtil.isNotEmpty(type)) {
            qMediaTypeSetting.type.eq(type);
        }

        return new SuccessResponseData(qMediaTypeSetting.orderBy().id.asc().findList());
    }

    @PostMapping("update")
    public ResponseData update(@RequestBody MediaTypeSetting mediaTypeSetting) {
        DB.update(mediaTypeSetting);
        return new SuccessResponseData(mediaTypeSetting);
    }
}
