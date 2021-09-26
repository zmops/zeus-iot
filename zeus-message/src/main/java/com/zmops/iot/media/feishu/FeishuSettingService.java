package com.zmops.iot.media.feishu;

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
public class FeishuSettingService {

    private volatile FeishuSettings instance;

    public FeishuSettings get() {
        return getOne();
    }

    private FeishuSettings getOne() {
        if (instance != null) {
            return instance;
        }
        MediaTypeSetting setting = new QMediaTypeSetting().type.eq("feishu").findOne();

        return instance = buildFeishuSetting(setting);
    }

    private FeishuSettings buildFeishuSetting(MediaTypeSetting setting) {
        JSONObject jsonObject = JSONObject.parseObject(setting.getWebhooks());
        String     secret     = Optional.ofNullable(jsonObject.getString("secret")).orElse("");
        String     url        = Optional.ofNullable(jsonObject.getString("url")).orElse("");
        return FeishuSettings.builder().textTemplate(setting.getTemplate())
                .webhooks(Arrays.asList(new FeishuSettings.WebHookUrl(secret, url))).build();
    }

    public void test() {

    }
}
