package com.zmops.iot.media.dingtalk;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.MediaTypeSetting;
import com.zmops.iot.domain.alarm.query.QMediaTypeSetting;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author yefei
 **/
@Service
public class DingtalkSettingService {

    private volatile DingtalkSettings instance;

    public DingtalkSettings get() {
        return getOne();
    }

    private DingtalkSettings getOne() {
        if (instance != null) {
            return instance;
        }
        MediaTypeSetting setting = new QMediaTypeSetting().type.eq("dingtalk").findOne();

        return instance = buildDingdingSetting(setting);
    }

    private DingtalkSettings buildDingdingSetting(MediaTypeSetting setting) {
        JSONObject jsonObject = JSONObject.parseObject(setting.getWebhooks());
        String     secret     = Optional.ofNullable(jsonObject.getString("secret")).orElse("");
        String     url        = Optional.ofNullable(jsonObject.getString("url")).orElse("");
        return DingtalkSettings.builder().textTemplate(setting.getTemplate())
                .webhooks(Arrays.asList(new DingtalkSettings.WebHookUrl(secret, url))).build();
    }


    public void test() {

    }
}
