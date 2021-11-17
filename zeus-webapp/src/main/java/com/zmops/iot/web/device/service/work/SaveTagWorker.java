package com.zmops.iot.web.device.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 * <p>
 * 设备标签处理步骤
 */
@Slf4j
@Component
public class SaveTagWorker implements IWorker<DeviceDto, Boolean> {

    @Override
    public Boolean action(DeviceDto deviceDto, Map<String, WorkerWrapper> allWrappers) {
        log.debug("step 2:SaveTagWorker----DEVICEID:{}…………",deviceDto.getDeviceId());

        String deviceId = deviceDto.getDeviceId();

        if (ToolUtil.isNotEmpty(deviceDto.getEdit()) && "true".equals(deviceDto.getEdit())) {
            //修改

            //没有修改关联的产品 不做处理
            if (deviceDto.getProductId().equals(deviceDto.getOldProductId())) {
                return true;
            }
            new QTag().sid.eq(deviceId).delete();
        }

        //继承产品中的标签
        DB.sqlUpdate("insert into tag (sid,tag,value,template_id) SELECT :deviceId,tag,value,id template_id from tag where sid=:sid")
                .setParameter("deviceId", deviceId).setParameter("sid", deviceDto.getProductId() + "").execute();
        log.debug("step 2:SaveTagWorker----DEVICEID:{} complete",deviceDto.getDeviceId());
        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }
}
