package com.zmops.iot.web.alarm.controller;


import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.messages.MailSetting;
import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.domain.messages.MailParam;
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
    public MailSetting get() {
        return settingService.get();
    }

    @PostMapping("test")
    public NoticeResult test(@Validated(MailParam.Test.class) @RequestBody MailParam mailParam) {
        return settingService.test(mailParam);
    }

    @PostMapping("update")
    public Integer update(@Validated(BaseEntity.Update.class) @RequestBody MailParam mailParam) {
        return settingService.updateSettings(mailParam.getSettings());
    }

}
