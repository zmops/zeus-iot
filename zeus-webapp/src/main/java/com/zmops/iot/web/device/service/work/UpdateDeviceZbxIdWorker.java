package com.zmops.iot.web.device.service.work;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.Device;
import com.zmops.zeus.server.async.callback.IWorker;
import com.zmops.zeus.server.async.wrapper.WorkerWrapper;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 * <p>
 * 更新设备中zbxID
 */
@Slf4j
@Component
public class UpdateDeviceZbxIdWorker implements IWorker<String, Boolean> {


    @Override
    public Boolean action(String deviceId, Map<String, WorkerWrapper> map) {

        Object result = map.get("saveZbxHostWork").getWorkResult().getResult();

        JSONArray hostid = JSONObject.parseObject(result.toString()).getJSONArray("hostids");

        log.debug("step 7:resolve zbxID async----DEVICEID:{}， HOSTID:{}…………", deviceId, hostid.get(0).toString());

        DB.update(Device.class).where().eq("deviceId", deviceId).asUpdate().set("zbxId", hostid.get(0).toString()).update();

        log.debug("step 7:resolve zbxID async----DEVICEID:{}， HOSTID:{} 完成", deviceId, hostid.get(0).toString());
        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }

}
