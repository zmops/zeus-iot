package com.zmops.iot.web.product.service;

import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.ProductServiceRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.domain.product.query.QProductService;
import com.zmops.iot.domain.product.query.QProductServiceParam;
import com.zmops.iot.domain.product.query.QProductServiceRelation;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.ProductServiceCreateEvent;
import com.zmops.iot.web.event.applicationEvent.ProductServiceUpdateEvent;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductServiceDto;
import com.zmops.iot.web.product.dto.param.ProductSvcParam;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 产品 物模型 服务
 **/
@Service
public class ProductSvcService implements CommandLineRunner {

    @Autowired
    ApplicationEventPublisher publisher;

    /**
     * 服务分页列表
     *
     * @param productSvcParam
     * @return
     */
    public Pager<ProductServiceDto> getServiceByPage(ProductSvcParam productSvcParam) {
        StringBuilder sql = new StringBuilder("SELECT s.*,r.inherit from product_service s LEFT JOIN product_service_relation r on r.service_id=s.id where 1=1 ");
        QProductService qProductService = new QProductService();
        if (ToolUtil.isNotEmpty(productSvcParam.getName())) {
            sql.append(" and s.name like :name");
            qProductService.name.contains(productSvcParam.getName());
        }
        if (ToolUtil.isNotEmpty(productSvcParam.getMark())) {
            sql.append(" and s.mark like :mark");
            qProductService.mark.contains(productSvcParam.getMark());
        }
        if (ToolUtil.isNotEmpty(productSvcParam.getProdId())) {
            sql.append(" and r.relation_id = :relationId");
            List<Long> serviceIds = getServiceIdList(productSvcParam.getProdId());
            if (ToolUtil.isNotEmpty(serviceIds)) {
                qProductService.id.in(serviceIds);
            }
        }
        sql.append(" order by id desc");
        DtoQuery<ProductServiceDto> query = DB.findDto(ProductServiceDto.class, sql.toString());
        if (ToolUtil.isNotEmpty(productSvcParam.getName())) {
            query.setParameter("name", "%" + productSvcParam.getName() + "%");
        }
        if (ToolUtil.isNotEmpty(productSvcParam.getMark())) {
            query.setParameter("mark", "%" + productSvcParam.getMark() + "%");
        }
        if (ToolUtil.isNotEmpty(productSvcParam.getProdId())) {
            query.setParameter("relationId", productSvcParam.getProdId());
        }

        List<ProductServiceDto> productServiceDtoList = query.setFirstRow((productSvcParam.getPage() - 1) * productSvcParam.getMaxRow())
                .setMaxRows(productSvcParam.getMaxRow()).findList();

        if (ToolUtil.isEmpty(productServiceDtoList)) {
            return new Pager<>(productServiceDtoList, 0);
        }
        //查询关联的参数
        List<Long> sids = productServiceDtoList.parallelStream().map(ProductServiceDto::getId).collect(Collectors.toList());
        List<ProductServiceParam> serviceParamList = new QProductServiceParam().serviceId.in(sids).deviceId.eq(productSvcParam.getProdId()).findList();
        Map<Long, List<ProductServiceParam>> map = serviceParamList.parallelStream().collect(Collectors.groupingBy(ProductServiceParam::getServiceId));
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
        if (ToolUtil.isNotEmpty(productSvcParam.getProdId())) {
            List<Long> serviceIds = getServiceIdList(productSvcParam.getProdId());
            if (ToolUtil.isEmpty(serviceIds)) {
                return Collections.emptyList();
            }
            qProdService.id.in(serviceIds);
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
    public ProductServiceDto create(ProductServiceDto productServiceDto) {
        //查询 是否已存在同名服务
        List<Long> serviceIds = getServiceIdList(productServiceDto.getRelationId());
        if (ToolUtil.isNotEmpty(serviceIds)) {
            int count = new QProductService().name.eq(productServiceDto.getName()).id.in(serviceIds).findCount();
            if (count > 0) {
                throw new ServiceException(BizExceptionEnum.SERVICE_EXISTS);
            }
        }
        //保存服务
        ProductService productService = new ProductService();
        ToolUtil.copyProperties(productServiceDto, productService);
        DB.save(productService);
        Long serviceId = productService.getId();
        productServiceDto.setId(serviceId);

        //保存服务与产品 关联关系
        ProductServiceRelation productServiceRelation = new ProductServiceRelation();
        productServiceRelation.setServiceId(productService.getId());
        productServiceRelation.setRelationId(productServiceDto.getRelationId());
        DB.save(productServiceRelation);

        //保存服务参数
        if (ToolUtil.isNotEmpty(productServiceDto.getProductServiceParamList())) {
            for (ProductServiceParam productServiceParam : productServiceDto.getProductServiceParamList()) {
                productServiceParam.setServiceId(null);
                productServiceParam.setServiceId(serviceId);
                productServiceParam.setDeviceId(productServiceDto.getRelationId());
            }
            DB.saveAll(productServiceDto.getProductServiceParamList());
        }

        //同步到设备

        if (ToolUtil.isNum(productServiceDto.getRelationId())) {
            publisher.publishEvent(new ProductServiceCreateEvent(this, productServiceDto));
        }
        updateService();
        return productServiceDto;
    }

    /**
     * 服务修改
     *
     * @param productServiceDto
     * @return
     */
    public ProductServiceDto update(ProductServiceDto productServiceDto) {
        //查询 是否已存在同名服务
        List<Long> serviceIds = getServiceIdList(productServiceDto.getRelationId());
        if (ToolUtil.isNotEmpty(serviceIds)) {
            int count = new QProductService().name.eq(productServiceDto.getName()).id.in(serviceIds)
                    .id.ne(productServiceDto.getId()).findCount();
            if (count > 0) {
                throw new ServiceException(BizExceptionEnum.SERVICE_EXISTS);
            }
        }

        //保存服务
        ProductService productService = new ProductService();
        ToolUtil.copyProperties(productServiceDto, productService);
        DB.update(productService);

        //重新保存服务参数
        new QProductServiceParam().serviceId.eq(productServiceDto.getId()).deviceId.eq(productServiceDto.getRelationId()).delete();
        if (ToolUtil.isNotEmpty(productServiceDto.getProductServiceParamList())) {
            for (ProductServiceParam productServiceParam : productServiceDto.getProductServiceParamList()) {
                productServiceParam.setServiceId(productServiceDto.getId());
                productServiceParam.setDeviceId(productServiceDto.getRelationId());
            }
            DB.saveAll(productServiceDto.getProductServiceParamList());
        }

        //同步到设备


        if (ToolUtil.isNum(productServiceDto.getRelationId())) {
            publisher.publishEvent(new ProductServiceUpdateEvent(this, productServiceDto));
        }
        updateService();
        return productServiceDto;
    }

    private List<Long> getServiceIdList(String relationId) {
        return new QProductServiceRelation().select(QProductServiceRelation.Alias.serviceId).relationId.eq(relationId)
                .findSingleAttributeList();
    }

    /**
     * 服务删除
     *
     * @param ids
     * @return
     */
    public void delete(List<Long> ids) {

        //检查服务是否被 告警 联动规则引用
        int count = new QProductEventService().serviceId.in(ids).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_EVENT_HAS_DEPTED);
        }

        new QProductServiceParam().serviceId.in(ids).delete();
        new QProductServiceRelation().serviceId.in(ids).delete();
        new QProductService().id.in(ids).delete();
    }

    /**
     * 根据服务 获取参数列表
     */
    public List<ProductServiceParam> paramList(long serviceId) {
        return new QProductServiceParam().serviceId.eq(serviceId).findList();
    }

    /**
     * 更新服务名称 服务参数 缓存
     */
    private void updateService() {
        List<ProductService> serviceList = new QProductService().findList();
        Map<Long, String> map = serviceList.parallelStream().collect(Collectors.toMap(ProductService::getId, ProductService::getName, (a, b) -> a));
        DefinitionsUtil.updateServiceCache(map);

        List<ProductServiceParam> serviceParamList = new QProductServiceParam().findList();
        Map<Long, List<ProductServiceParam>> paramMap = serviceParamList.parallelStream().collect(Collectors.groupingBy(ProductServiceParam::getServiceId));
        DefinitionsUtil.updateServiceParamCache(paramMap);
    }

    @Override
    public void run(String... args) throws Exception {
        updateService();
    }
}
