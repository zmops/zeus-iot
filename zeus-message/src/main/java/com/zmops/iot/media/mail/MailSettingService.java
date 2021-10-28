package com.zmops.iot.media.mail;


import com.zmops.iot.domain.messages.MailParam;
import com.zmops.iot.domain.messages.MailSetting;
import com.zmops.iot.domain.messages.NoticeResult;

/**
 * @author yefei
 * @email yefei@zmops.com
 * @date Created in  2020/11/10 18:58
 * @Description
 */
public interface MailSettingService {

    MailSetting get();
    NoticeResult test(MailParam mailTestVo);
    Integer updateSettings(MailSetting mailTestVo);
}
