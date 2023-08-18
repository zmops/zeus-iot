package com.zmops.iot.media.feishu;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.MediaTypeSetting;
import com.zmops.iot.domain.alarm.query.QMediaTypeSetting;
import com.zmops.iot.media.dingtalk.DingtalkSettings;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yefei
 **/
@Service
public class FeishuSettingService {

    private volatile FeishuSettings instance;

    private volatile Map<Long, FeishuSettings> instanceMap = new ConcurrentHashMap<>();

    public FeishuSettings get(Long tenantId) {
        if (null == tenantId) {
            return getOne();
        } else {
            return getFromMap(tenantId);
        }
    }

    private FeishuSettings getFromMap(Long tenantId) {
        if (instanceMap.get(tenantId) != null) {
            return instanceMap.get(tenantId);
        }
        MediaTypeSetting setting = new QMediaTypeSetting().type.eq("feishu").tenantId.eq(tenantId).findOne();
        FeishuSettings instance = buildFeishuSetting(setting);
        instanceMap.put(tenantId,instance);
        return instance;
    }

    private FeishuSettings getOne() {
        if (instance != null) {
            return instance;
        }
        MediaTypeSetting setting = new QMediaTypeSetting().type.eq("feishu").tenantId.isNull().findOne();

        return instance = buildFeishuSetting(setting);
    }

    private FeishuSettings buildFeishuSetting(MediaTypeSetting setting) {
        JSONObject jsonObject = JSONObject.parseObject(setting.getWebhooks());
        String secret = Optional.ofNullable(jsonObject.getString("secret")).orElse("");
        String url = Optional.ofNullable(jsonObject.getString("url")).orElse("");
        return FeishuSettings.builder().textTemplate(setting.getTemplate())
                .webhooks(Arrays.asList(new FeishuSettings.WebHookUrl(secret, url))).build();
    }

    public void test() {

    }
}
