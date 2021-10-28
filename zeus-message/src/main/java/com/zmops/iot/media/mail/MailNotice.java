package com.zmops.iot.media.mail;


import com.zmops.iot.domain.messages.MailSetting;
import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.media.Notice;

public interface MailNotice extends Notice {

    NoticeResult test(MailSetting setting, String receiver);
}
