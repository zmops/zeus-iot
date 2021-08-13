package com.zmops.iot.mediaType.welink;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.MediaTypeSetting;
import com.zmops.iot.domain.alarm.query.QMediaTypeSetting;
import com.zmops.iot.mediaType.wechat.WechatSettings;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * @author yefei
 **/
@Service
public class WeLinkSettingService {

    private volatile WeLinkSettings instance;

    public WeLinkSettings get() {
        return getOne();
    }

    private WeLinkSettings getOne() {
        if (instance != null) {
            return instance;
        }
        MediaTypeSetting setting = new QMediaTypeSetting().type.eq("welink").findOne();

        return instance = buildWeLinkSetting(setting);
    }

    private WeLinkSettings buildWeLinkSetting(MediaTypeSetting setting) {
        Map map = JSONObject.parseObject(setting.getWebhooks(), Map.class);
//        String     clientId     = Optional.ofNullable(jsonObject.getString("clientId")).orElse("");
//        String     clientSecret        = Optional.ofNullable(jsonObject.getString("clientSecret")).orElse("");
//        String     accessTokenUrl        = Optional.ofNullable(jsonObject.getString("accessTokenUrl")).orElse("");
//        String     messageUrl        = Optional.ofNullable(jsonObject.getString("messageUrl")).orElse("");
        return WeLinkSettings.builder().textTemplate(setting.getTemplate())
                .webhooks(Arrays.asList(WeLinkSettings.WebHookUrl.generateFromMap(map))).build();
    }


    public void updateSettings(MediaTypeSetting WechatSetting) {


    }

    public void test() {

    }
}
