package com.zmops.iot.web.alarm.controller;

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
        QMediaTypeSetting qMediaTypeSetting = new QMediaTypeSetting();
        if (ToolUtil.isNotEmpty(type)) {
            qMediaTypeSetting.type.eq(type);
        }
        return new SuccessResponseData(qMediaTypeSetting.findList());
    }

    @PostMapping("update")
    public ResponseData update(@RequestBody MediaTypeSetting mediaTypeSetting) {
        DB.update(mediaTypeSetting);
        return new SuccessResponseData(mediaTypeSetting);
    }
}
