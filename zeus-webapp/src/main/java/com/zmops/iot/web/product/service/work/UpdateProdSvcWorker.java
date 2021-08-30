package com.zmops.iot.web.product.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductService;
import com.zmops.iot.domain.product.query.QProductServiceParam;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductServiceDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Long Id = productServiceDto.getId();

        List<ProductService> list = new QProductService().templateId.eq(Id).findList();

        for (ProductService productService : list) {
            productService.setName(productServiceDto.getName());
            productService.setMark(productServiceDto.getMark());
            productService.setAsync(productServiceDto.getAsync());
        }
        DB.updateAll(list);

        //处理服务参数
        List<Long> ids = list.parallelStream().map(ProductService::getId).collect(Collectors.toList());
        new QProductServiceParam().serviceId.in(ids).delete();
        if (ToolUtil.isNotEmpty(productServiceDto.getProductServiceParamList())) {
            for (Long id : ids) {
                for (ProductServiceParam productServiceParam : productServiceDto.getProductServiceParamList()) {
                    productServiceParam.setServiceId(id);
                }
            }
            DB.saveAll(productServiceDto.getProductServiceParamList());
        }
        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
