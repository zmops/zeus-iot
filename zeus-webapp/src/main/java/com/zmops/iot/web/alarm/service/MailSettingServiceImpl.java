package com.zmops.iot.web.alarm.service;


import com.zmops.iot.domain.messages.MailParam;
import com.zmops.iot.domain.messages.MailSetting;
import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.domain.messages.query.QMailSetting;
import com.zmops.iot.media.mail.MailNotice;
import com.zmops.iot.media.mail.MailSettingService;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author yefei
 **/
@Service
@Slf4j
public class MailSettingServiceImpl implements MailSettingService {

    @Autowired
    MailNotice mailNotice;

    private volatile MailSetting instance;

    @Override
    public MailSetting get() {
        return Optional.ofNullable(getOne()).orElse(new MailSetting());
    }

    private MailSetting getOne() {
        if (instance != null) {
            return instance;
        }
        return instance = Optional.of(new QMailSetting().findList())
                .map(Collection::iterator)
                .filter(Iterator::hasNext)
                .map(Iterator::next).orElse(null);
    }


    @Override
    public NoticeResult test(MailParam mailParam) {
        MailSetting mailSetting = mailParam.getSettings();
        NoticeResult noticeResult = mailNotice.test(mailSetting, mailParam.getReceiver());
        //updateSettings(noticeResult, mailSetting);
        return noticeResult;
    }

    @Override
    public Integer updateSettings(MailSetting mailSetting) {
        if (mailSetting.getTls() == null) {
            mailSetting.setTls(0);
        }
        if (mailSetting.getSsl() == null) {
            mailSetting.setSsl(0);
        }
        MailSetting dbSetting = getOne();
        if (dbSetting == null) {
            DB.insert(mailSetting);
        } else {
            mailSetting.setId(dbSetting.getId());
            DB.update(mailSetting);
        }
        instance = mailSetting;
        return mailSetting.getId();
    }

}
