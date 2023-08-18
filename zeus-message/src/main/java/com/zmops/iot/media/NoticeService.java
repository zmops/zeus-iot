package com.zmops.iot.media;


import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.domain.messages.query.QNoticeRecord;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.util.LocalDateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class NoticeService {


    @Autowired
    Collection<Notice> notices;

    public Map<Integer, NoticeResult> notice(SysUser sysUser, Map<String, String> macroMap,String triggerId) {
//        Problem problem = problemMapper.selectById(Integer.parseInt(macroMap.get("${eventId}")));
        Map<Integer, NoticeResult> res = new HashMap<>();
        notices.forEach(notice -> {
            int type = notice.getType();

            QNoticeRecord eq = new QNoticeRecord().noticeType
                    .eq(type).creatTime
                    .ge(LocalDateTimeUtils.minu(LocalDateTime.now(), notice.getSilent(), ChronoUnit.MINUTES))
                    .problemId.eq(triggerId);

            if (eq.findCount() > 0) {
                return;
            }
            try {
                NoticeResult send = notice.send(sysUser, macroMap);
                if (send.getStatus() != NoticeResult.NoticeStatus.skipped) {
                    res.put(notice.getType(), send);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return res;
    }
}
