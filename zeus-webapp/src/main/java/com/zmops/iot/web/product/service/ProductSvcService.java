package com.zmops.iot.web.product.service;

import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductService;
import com.zmops.iot.domain.product.query.QProductServiceParam;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductServiceDto;
import com.zmops.iot.web.product.dto.param.ProductSvcParam;
import io.ebean.DB;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 产品 物模型 服务
 **/
@Service
public class ProductSvcService {

    /**
     * 服务分页列表
     *
     * @param productSvcParam
     * @return
     */
    public Pager<ProductServiceDto> getServiceByPage(ProductSvcParam productSvcParam) {
        QProductService qProductService = new QProductService();
        if (ToolUtil.isNotEmpty(productSvcParam.getName())) {
            qProductService.name.contains(productSvcParam.getName());
        }
        if (ToolUtil.isNotEmpty(productSvcParam.getMark())) {
            qProductService.mark.contains(productSvcParam.getMark());
        }
        qProductService.orderBy(" id desc");
        List<ProductServiceDto> productServiceDtoList = qProductService.setFirstRow((productSvcParam.getPage() - 1) * productSvcParam.getMaxRow())
                .setMaxRows(productSvcParam.getMaxRow()).asDto(ProductServiceDto.class).findList();

        if (ToolUtil.isEmpty(productServiceDtoList)) {
            return new Pager<>(productServiceDtoList, 0);
        }
        //查询关联的参数
        List<Long>                           sids             = productServiceDtoList.parallelStream().map(ProductServiceDto::getId).collect(Collectors.toList());
        List<ProductServiceParam>            serviceParamList = new QProductServiceParam().serviceId.in(sids).findList();
        Map<Long, List<ProductServiceParam>> map              = serviceParamList.parallelStream().collect(Collectors.groupingBy(ProductServiceParam::getServiceId));
        productServiceDtoList.forEach(productServiceDto -> {
            if (null != map.get(productServiceDto.getId())) {
                productServiceDto.setProductServiceParamList(map.get(productServiceDto.getId()));
            }
        });
        return new Pager<>(productServiceDtoList, qProductService.findCount());
    }

    /**
     * 服务列表
     *
     * @param productSvcParam
     * @return
     */
    public List<ProductService> list(ProductSvcParam productSvcParam) {
        QProductService qProdService = new QProductService();
        if (ToolUtil.isNotEmpty(productSvcParam.getName())) {
            qProdService.name.contains(productSvcParam.getName());
        }
        if (ToolUtil.isNotEmpty(productSvcParam.getMark())) {
            qProdService.mark.contains(productSvcParam.getMark());
        }
        qProdService.orderBy(" id desc");
        return qProdService.findList();
    }

    /**
     * 服务创建
     *
     * @param productServiceDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Object create(ProductServiceDto productServiceDto) {
        int count = new QProductService().name.eq(productServiceDto.getName()).sid.eq(productServiceDto.getSid()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.SERVICE_EXISTS);
        }
        ProductService productService = new ProductService();
        ToolUtil.copyProperties(productServiceDto, productService);
        DB.save(productService);
        Long serviceId = productService.getId();
        productServiceDto.setId(serviceId);

        if (ToolUtil.isNotEmpty(productServiceDto.getProductServiceParamList())) {
            for (ProductServiceParam productServiceParam : productServiceDto.getProductServiceParamList()) {
                productServiceParam.setServiceId(serviceId);
            }
            DB.saveAll(productServiceDto.getProductServiceParamList());
        }

        //TODO 同步到设备
        return productServiceDto;
    }

    /**
     * 服务修改
     *
     * @param productServiceDto
     * @return
     */
    public Object update(ProductServiceDto productServiceDto) {
        int count = new QProductService().name.eq(productServiceDto.getName()).sid.eq(productServiceDto.getSid())
                .id.ne(productServiceDto.getId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.SERVICE_EXISTS);
        }
        ProductService productService = new ProductService();
        ToolUtil.copyProperties(productServiceDto, productService);
        DB.save(productService);

        new QProductServiceParam().serviceId.eq(productServiceDto.getId()).delete();
        if (ToolUtil.isNotEmpty(productServiceDto.getProductServiceParamList())) {
            for (ProductServiceParam productServiceParam : productServiceDto.getProductServiceParamList()) {
                productServiceParam.setServiceId(productServiceDto.getId());
            }
            DB.saveAll(productServiceDto.getProductServiceParamList());
        }
        //TODO 同步到设备

        return productServiceDto;
    }

    /**
     * 服务删除
     *
     * @param ids
     * @return
     */
    public void delete(List<Long> ids) {

        new QProductServiceParam().serviceId.in(ids).delete();
        new QProductService().id.in(ids).delete();

        //TODO 同步到设备
    }
}
