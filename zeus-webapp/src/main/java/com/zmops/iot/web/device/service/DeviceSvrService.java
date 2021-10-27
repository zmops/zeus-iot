package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.Forest;
import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductService;
import com.zmops.iot.domain.product.query.QProductServiceParam;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class DeviceSvrService {

    public void execute(String deviceId, Long serviceId) {

        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map = new ConcurrentHashMap<>(2);
//        Device device = new QDevice().deviceId.eq(deviceId).findOne();
//        if (null == device) {
//            throw new ServiceException(BizExceptionEnum.DEVICE_NOT_EXISTS);
//        }
        map.put("device", deviceId);

        List<Map<String, Object>> serviceList = new ArrayList<>();
        Map<String, Object> serviceMap = new ConcurrentHashMap<>();
        ProductService productService = new QProductService().id.eq(serviceId).findOne();
        if (null == productService) {
            throw new ServiceException(BizExceptionEnum.SERVICE_NOT_EXISTS);
        }
        serviceMap.put("name", productService.getName());

        List<ProductServiceParam> paramList = new QProductServiceParam().serviceId.eq(serviceId).findList();
        if (ToolUtil.isNotEmpty(paramList)) {
            serviceMap.put("param", paramList.parallelStream().collect(Collectors.toMap(ProductServiceParam::getKey, ProductServiceParam::getValue)));
        }
        serviceList.add(serviceMap);

        map.put("service", serviceList);
        list.add(map);

        Forest.post("/device/action/exec").host("127.0.0.1").port(12800).contentTypeJson().addBody(JSON.toJSON(list)).execute();
    }
}
