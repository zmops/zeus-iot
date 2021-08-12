package com.zmops.iot.mediaType.wechat;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.MediaTypeSetting;
import com.zmops.iot.domain.alarm.query.QMediaTypeSetting;
import com.zmops.iot.mediaType.dingtalk.DingtalkSettings;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author yefei
 **/
@Service
public class WechatSettingService {

    private volatile WechatSettings instance;

    public WechatSettings get() {
        return getOne();
    }

    private WechatSettings getOne() {
        if (instance != null) {
            return instance;
        }
        MediaTypeSetting setting = new QMediaTypeSetting().type.eq("wechat").findOne();

        return instance = buildWechatSetting(setting);
    }

    private WechatSettings buildWechatSetting(MediaTypeSetting setting) {
        JSONObject jsonObject = JSONObject.parseObject(setting.getWebhooks());
//        String     secret     = Optional.ofNullable(jsonObject.getString("secret")).orElse("");
//        String     url        = Optional.ofNullable(jsonObject.getString("url")).orElse("");
        return WechatSettings.builder().textTemplate(setting.getTemplate())
                .webhooks(Arrays.asList("")).build();
    }


    public void test() {

    }
}
