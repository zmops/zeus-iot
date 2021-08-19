package com.zmops.iot.web.device.service.work;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 *
 * 更新设备中zbxID
 */
@Slf4j
@Component
public class UpdateDeviceZbxIdWorker implements IWorker<String, Boolean> {


    @Override
    public Boolean action(String deviceDto, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理 zbxID 回填工作…………");

        Object    result = map.get("saveZbxHostWork").getWorkResult().getResult();
        JSONArray hostid = JSONObject.parseObject(result.toString()).getJSONArray("hostids");
        Device    device = (Device) map.get("saveDvice").getWorkResult().getResult();
        device.setZbxId(hostid.get(0).toString());
        DB.update(device);

        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }

}
