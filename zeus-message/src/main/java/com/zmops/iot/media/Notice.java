package com.zmops.iot.media;


import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.domain.sys.SysUser;

import java.util.Map;

public interface Notice {
    int sms      = 1;
    int email    = 2;
    int wechat   = 3;
    int dingtalk = 4;

    /**
     * 发送通知
     *
     * @param receiver
     * @param macroMap
     * @return
     */
    NoticeResult send(SysUser receiver, Map<String, String> macroMap);

    int getType();

    int getSilent();
}
