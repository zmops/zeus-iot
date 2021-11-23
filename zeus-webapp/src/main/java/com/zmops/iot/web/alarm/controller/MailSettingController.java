package com.zmops.iot.web.alarm.controller;


import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.messages.MailSetting;
import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.domain.messages.MailParam;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.alarm.service.MailSettingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 *
 **/
@RestController
@RequestMapping("mailSetting")
public class MailSettingController {

    @Autowired
    MailSettingServiceImpl settingService;

    @RequestMapping("get")
    public ResponseData get() {
        return ResponseData.success(settingService.get());
    }

    @PostMapping("test")
    public ResponseData test(@Validated(MailParam.Test.class) @RequestBody MailParam mailParam) {
        NoticeResult test = settingService.test(mailParam);
        if(test.getStatus().equals(NoticeResult.NoticeStatus.success)){
            return ResponseData.success(test.getMsg());
        }else{
            return ResponseData.error(test.getMsg());
        }
    }

    @PostMapping("update")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody MailParam mailParam) {
        return ResponseData.success(settingService.updateSettings(mailParam.getSettings()));
    }

}
