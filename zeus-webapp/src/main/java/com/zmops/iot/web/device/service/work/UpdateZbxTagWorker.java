package com.zmops.iot.web.device.service.work;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.Tag;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.zeus.driver.service.ZbxHost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * <p>
 * 同步设备标签到zbx
 */
@Slf4j
@Component
public class UpdateZbxTagWorker implements IWorker<DeviceDto, Boolean> {

    @Autowired
    ZbxHost zbxHost;

    @Override
    public Boolean action(DeviceDto deviceDto, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("同步 zbx tag 工作……");
        //取出 ZBX hostid
        Object result = map.get("saveZbxHostWork").getWorkResult().getResult();
        JSONArray hostid = JSONObject.parseObject(result.toString()).getJSONArray("hostids");

        //取 设备ID
        Device device = (Device) map.get("saveDvice").getWorkResult().getResult();
        String deviceId = device.getDeviceId();

        //查询出本地tag
        List<Tag> list = new QTag().sid.eq(deviceId).findList();
        Map<String, String> tagMap = new HashMap<>(list.size());
        for (Tag tag : list) {
            tagMap.put(tag.getTag(), tag.getValue());
        }

        //保存
        zbxHost.hostTagUpdate(hostid.getString(0), tagMap);
        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }

}
