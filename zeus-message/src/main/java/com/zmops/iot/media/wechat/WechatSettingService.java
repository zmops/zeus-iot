package com.zmops.iot.media.wechat;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.MediaTypeSetting;
import com.zmops.iot.domain.alarm.query.QMediaTypeSetting;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yefei
 **/
@Service
public class WechatSettingService {

    private volatile WechatSettings instance;

    private volatile Map<Long, WechatSettings> instanceMap = new ConcurrentHashMap<>();

    public WechatSettings get(Long tenantId) {
        if (null == tenantId) {
            return getOne();
        } else {
            return getFromMap(tenantId);
        }
    }

    private WechatSettings getFromMap(Long tenantId) {
        if (instanceMap.get(tenantId) != null) {
            return instanceMap.get(tenantId);
        }
        MediaTypeSetting setting  = new QMediaTypeSetting().type.eq("wechat").tenantId.eq(tenantId).findOne();
        WechatSettings   instance = buildWechatSetting(setting);
        instanceMap.put(tenantId, instance);
        return instance;
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
