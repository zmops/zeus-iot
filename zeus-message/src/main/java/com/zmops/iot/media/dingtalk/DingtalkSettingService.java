package com.zmops.iot.media.dingtalk;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.MediaTypeSetting;
import com.zmops.iot.domain.alarm.query.QMediaTypeSetting;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yefei
 **/
@Service
public class DingtalkSettingService {

    private volatile DingtalkSettings instance;

    private volatile Map<Long, DingtalkSettings> instanceMap = new ConcurrentHashMap<>();

    public DingtalkSettings get(Long tenantId) {
        if (null == tenantId) {
            return getOne();
        } else {
            return getFromMap(tenantId);
        }

    }

    private DingtalkSettings getFromMap(Long tenantId) {
        if (instanceMap.get(tenantId) != null) {
            return instanceMap.get(tenantId);
        }
        MediaTypeSetting setting = new QMediaTypeSetting().type.eq("dingtalk").tenantId.eq(tenantId).findOne();
        DingtalkSettings instance = buildDingdingSetting(setting);
        instanceMap.put(tenantId,instance);
        return instance;
    }

    private DingtalkSettings getOne() {
        if (instance != null) {
            return instance;
        }
        MediaTypeSetting setting = new QMediaTypeSetting().type.eq("dingtalk").tenantId.isNull().findOne();

        return instance = buildDingdingSetting(setting);
    }

    private DingtalkSettings buildDingdingSetting(MediaTypeSetting setting) {
        JSONObject jsonObject = JSONObject.parseObject(setting.getWebhooks());
        String secret = Optional.ofNullable(jsonObject.getString("secret")).orElse("");
        String url = Optional.ofNullable(jsonObject.getString("url")).orElse("");
        return DingtalkSettings.builder().textTemplate(setting.getTemplate())
                .webhooks(Arrays.asList(new DingtalkSettings.WebHookUrl(secret, url))).build();
    }


    public void test() {

    }
}
