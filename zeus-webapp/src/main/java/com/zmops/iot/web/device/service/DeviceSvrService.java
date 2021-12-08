package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.ServiceExecuteRecord;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductService;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import io.ebean.DB;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 设备服务
 **/
@Service
public class DeviceSvrService {

    /**
     * 执行指定设备的某个服务
     *
     * @param deviceId  设备ID
     * @param serviceId 服务ID
     */
    public void execute(String deviceId, Long serviceId) {

        //封装执行参数
        List<Map<String, Object>> body = new ArrayList<>();

        Map<String, Object> map = new ConcurrentHashMap<>(2);

        map.put("device", deviceId);

        List<Map<String, Object>> serviceList = new ArrayList<>();
        Map<String, Object> serviceMap = new ConcurrentHashMap<>();
        ProductService productService = new QProductService().id.eq(serviceId).findOne();
        if (null == productService) {
            throw new ServiceException(BizExceptionEnum.SERVICE_NOT_EXISTS);
        }
        serviceMap.put("name", productService.getName());

        List<ProductServiceParam> paramList = DefinitionsUtil.getServiceParam(serviceId);
        if (ToolUtil.isNotEmpty(paramList)) {
            serviceMap.put("param", paramList.parallelStream().collect(Collectors.toMap(ProductServiceParam::getKey, ProductServiceParam::getValue)));
        }
        serviceList.add(serviceMap);

        map.put("service", serviceList);
        body.add(map);

        //下发命令 执行
        Forest.post("/device/action/exec").host("127.0.0.1").port(12800).contentTypeJson().addBody(JSON.toJSON(body)).execute();

        //记录服务日志
        ServiceExecuteRecord serviceExecuteRecord = new ServiceExecuteRecord();
        serviceExecuteRecord.setDeviceId(deviceId);
        if (ToolUtil.isNotEmpty(paramList)) {
            serviceExecuteRecord.setParam(JSONObject.toJSONString(paramList.parallelStream().collect(Collectors.toMap(ProductServiceParam::getKey, ProductServiceParam::getValue))));
        }
        serviceExecuteRecord.setServiceName(DefinitionsUtil.getServiceName(serviceId));

        Device device = new QDevice().deviceId.eq(deviceId).findOne();
        if (null != device) {
            serviceExecuteRecord.setTenantId(device.getTenantId());
        }

        serviceExecuteRecord.setCreateTime(LocalDateTime.now());
        serviceExecuteRecord.setExecuteType("手动");
        serviceExecuteRecord.setExecuteUser(LoginContextHolder.getContext().getUserId());

        DB.insert(serviceExecuteRecord);
    }
}
