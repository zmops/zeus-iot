package com.zmops.iot.web.product.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.web.product.dto.ProductServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 * <p>
 * 产品服务修改 同步到设备 步骤
 */
@Slf4j
@Component
public class UpdateProdSvcWorker implements IWorker<ProductServiceDto, Boolean> {


    @Override
    public Boolean action(ProductServiceDto productServiceDto, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理产品服务修改 同步到设备工作…………");


        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
