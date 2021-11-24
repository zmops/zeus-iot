package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.iot.web.product.dto.ProductAttrDto;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.iot.web.product.dto.param.ProductAttrParam;
import com.zmops.iot.web.product.service.work.AsyncAttrZbxIdWorker;
import com.zmops.iot.web.product.service.work.SaveProdAttrWorker;
import com.zmops.iot.web.product.service.work.UpdateAttributeWorker;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.entity.ZbxProcessingStep;
import com.zmops.zeus.driver.service.ZbxItem;
import com.zmops.zeus.server.async.executor.Async;
import com.zmops.zeus.server.async.wrapper.WorkerWrapper;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class ProductModelService {

    @Autowired
    private ZbxItem zbxItem;

    @Autowired
    SaveProdAttrWorker saveProdAttrWorker;

    @Autowired
    AsyncAttrZbxIdWorker asyncAttrZbxIdWorker;

    @Autowired
    UpdateAttributeWorker updateProdAttrWorker;

    /**
     * 产品属性分页列表
     *
     * @param productAttr
     * @return
     */
    public Pager<ProductAttrDto> prodModelAttributeList(ProductAttrParam productAttr) {
        QProductAttribute qProductAttribute = new QProductAttribute();

        if (ToolUtil.isNotEmpty(productAttr.getProdId())) {
            qProductAttribute.productId.eq(productAttr.getProdId());
        }
        if (ToolUtil.isNotEmpty(productAttr.getAttrName())) {
            qProductAttribute.name.contains(productAttr.getAttrName());
        }
        if (ToolUtil.isNotEmpty(productAttr.getKey())) {
            qProductAttribute.key.contains(productAttr.getKey());
        }

        List<ProductAttrDto> pagedList = qProductAttribute
                .setFirstRow((productAttr.getPage() - 1) * productAttr.getMaxRow())
                .setMaxRows(productAttr.getMaxRow()).orderBy(" create_time desc ")
                .asDto(ProductAttrDto.class).findList();

        int count = qProductAttribute.findCount();
        return new Pager<>(pagedList, count);
    }

    /**
     * 产品属性列表
     *
     * @param productAttr
     * @return
     */
    public List<ProductAttrDto> list(ProductAttrParam productAttr) {
//        QProductAttribute qProductAttribute = new QProductAttribute();
//        if (null != productAttr.getProdId()) {
//            qProductAttribute.productId.eq(productAttr.getProdId());
//        }
//        if (ToolUtil.isNotEmpty(productAttr.getAttrName())) {
//            qProductAttribute.name.contains(productAttr.getAttrName());
//        }
//        if (ToolUtil.isNotEmpty(productAttr.getKey())) {
//            qProductAttribute.key.contains(productAttr.getKey());
//        }
//        return qProductAttribute.orderBy(" create_time desc").asDto(ProductAttrDto.class).findList();
        StringBuilder sql = new StringBuilder("select * from product_attribute where 1=1");
        if (null != productAttr.getProdId()) {
            sql.append(" and product_id = :productId");
        }
        if (ToolUtil.isNotEmpty(productAttr.getAttrName())) {
            sql.append(" and name like :attrName");
        }
        if (ToolUtil.isNotEmpty(productAttr.getKey())) {
            sql.append(" and key like :key");
        }
        sql.append(" order by create_time desc");
        DtoQuery<ProductAttrDto> dto = DB.findDto(ProductAttrDto.class, sql.toString());

        if (null != productAttr.getProdId()) {
            dto.setParameter("productId", productAttr.getProdId());
        }
        if (ToolUtil.isNotEmpty(productAttr.getAttrName())) {
            dto.setParameter("attrName", "%" + productAttr.getAttrName() + "%");
        }
        if (ToolUtil.isNotEmpty(productAttr.getKey())) {
            dto.setParameter("key", "%" + productAttr.getKey() + "%");
        }

        return dto.findList();
    }

    /**
     * 产品属性详情
     *
     * @param attrId
     * @return
     */
    public ProductAttrDto detail(Long attrId) {
//        ProductAttrDto attr = new QProductAttribute().attrId.eq(attrId).asDto(ProductAttrDto.class).findOne();
        ProductAttrDto attr = DB.findDto(ProductAttrDto.class, "select * from product_attribute where attr_id=:attrId").setParameter("attrId", attrId).findOne();

        if (attr == null || ToolUtil.isEmpty(attr.getZbxId())) {
            return attr;
        }
        JSONArray itemInfo = JSONObject.parseArray(zbxItem.getItemInfo(attr.getZbxId(), null));
        attr.setTags(JSONObject.parseArray(itemInfo.getJSONObject(0).getString("tags"), ProductTag.Tag.class));
        attr.setProcessStepList(formatProcessStep(itemInfo.getJSONObject(0).getString("preprocessing")));
        String valuemap = itemInfo.getJSONObject(0).getString("valuemap");

        if (ToolUtil.isNotEmpty(valuemap) && !"[]".equals(valuemap)) {
            attr.setValuemapid(JSONObject.parseObject(valuemap).getString("valuemapid"));
        }

        return attr;
    }

    private List<ProductAttr.ProcessingStep> formatProcessStep(String preprocessing) {
        if (ToolUtil.isEmpty(preprocessing)) {
            return Collections.emptyList();
        }
        List<ProductAttr.ProcessingStep> processingSteps = new ArrayList<>();
        JSONArray jsonArray = JSONObject.parseArray(preprocessing);
        for (Object object : jsonArray) {
            ProductAttr.ProcessingStep processingStep = new ProductAttr.ProcessingStep();
            processingStep.setType(((JSONObject) object).getString("type"));
            processingStep.setParams(((JSONObject) object).getString("params").split("\\n"));
            processingSteps.add(processingStep);
        }

        return processingSteps;
    }

    /**
     * 创建 产品属性
     *
     * @param productAttr 产品属性DTO
     */
    public void createProductAttr(ProductAttr productAttr, String zbxId) {
        ProductAttribute productAttribute = new ProductAttribute();
        buildProdAttribute(productAttribute, productAttr);
        productAttribute.setZbxId(zbxId);
        productAttribute.save();


        WorkerWrapper<ProductAttr, Boolean> asyncAttrZbxIdWork = new WorkerWrapper.Builder<ProductAttr, Boolean>()
                .worker(asyncAttrZbxIdWorker).param(productAttr).build();
        WorkerWrapper<ProductAttr, Boolean> saveProdAttrWork = new WorkerWrapper.Builder<ProductAttr, Boolean>()
                .worker(saveProdAttrWorker).param(productAttr).next(asyncAttrZbxIdWork).build();

        try {
            Async.beginWork(10000, saveProdAttrWork);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private ProductAttribute buildProdAttribute(ProductAttribute prodAttribute, ProductAttr productAttr) {
        prodAttribute.setProductId(productAttr.getProductId());
        prodAttribute.setName(productAttr.getAttrName());
        prodAttribute.setKey(productAttr.getKey());
        prodAttribute.setSource(productAttr.getSource());
        prodAttribute.setUnits(productAttr.getUnits());
        prodAttribute.setRemark(productAttr.getRemark());
        prodAttribute.setValueType(productAttr.getValueType());
        prodAttribute.setAttrId(productAttr.getAttrId());
        prodAttribute.setDepAttrId(productAttr.getDepAttrId());
        prodAttribute.setValuemapid(productAttr.getValuemapid());
        prodAttribute.setUnit(productAttr.getUnit());

        return prodAttribute;
    }

    /**
     * 创建 Trapper 类型 ITEM
     *
     * @param productAttr 属性
     * @return String
     */
    public String createTrapperItem(ProductAttr productAttr) {

        String itemName = productAttr.getAttrId() + "";

        Product prod = new QProduct().productId.eq(Long.parseLong(productAttr.getProductId())).findOne();
        if (null == prod) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        String hostId = prod.getZbxId();

        List<ZbxProcessingStep> processingSteps = new ArrayList<>();
        if (ToolUtil.isNotEmpty(productAttr.getProcessStepList())) {
            productAttr.getProcessStepList().forEach(i -> {
                ZbxProcessingStep step = new ZbxProcessingStep();

                step.setType(i.getType());
                step.setParams(i.getParams());

                processingSteps.add(step);
            });
        }
        Map<String, String> tagMap = new HashMap<>();
        if (ToolUtil.isNotEmpty(productAttr.getTags())) {
            tagMap = productAttr.getTags().stream()
                    .collect(Collectors.toMap(ProductTag.Tag::getTag, ProductTag.Tag::getValue, (k1, k2) -> k2));
        }

        return zbxItem.createTrapperItem(itemName, productAttr.getKey(),
                hostId, productAttr.getSource(), productAttr.getMasterItemId(), productAttr.getValueType(), productAttr.getUnits(), processingSteps, productAttr.getValuemapid(), tagMap);
    }

    /**
     * 修改 Trapper 类型 ITEM
     *
     * @param productAttr 属性
     * @return String
     */
    public ProductAttr updateTrapperItem(ProductAttr productAttr) {
        ProductAttribute productAttribute = new QProductAttribute().attrId.eq(productAttr.getAttrId()).findOne();
        buildProdAttribute(productAttribute, productAttr);
        Product prod = new QProduct().productId.eq(Long.parseLong(productAttr.getProductId())).findOne();
        if (null == prod) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        String hostId = prod.getZbxId();
        List<ZbxProcessingStep> processingSteps = new ArrayList<>();
        if (ToolUtil.isNotEmpty(productAttr.getProcessStepList())) {
            productAttr.getProcessStepList().forEach(i -> {
                ZbxProcessingStep step = new ZbxProcessingStep();

                step.setType(i.getType());
                step.setParams(i.getParams());

                processingSteps.add(step);
            });
        }

        Map<String, String> tagMap = new HashMap<>();
        if (ToolUtil.isNotEmpty(productAttr.getTags())) {
            tagMap = productAttr.getTags().stream()
                    .collect(Collectors.toMap(ProductTag.Tag::getTag, ProductTag.Tag::getValue, (k1, k2) -> k2));
        }


        zbxItem.updateTrapperItem(productAttribute.getZbxId(), productAttr.getAttrId() + "", productAttr.getKey(),
                hostId, productAttr.getSource(), productAttr.getMasterItemId(), productAttr.getValueType(), productAttr.getUnits(), processingSteps, productAttr.getValuemapid(), tagMap);

        DB.update(productAttribute);

        WorkerWrapper<ProductAttr, Boolean> updateProdAttrWork = new WorkerWrapper.Builder<ProductAttr, Boolean>().worker(updateProdAttrWorker).param(productAttr).build();

        try {
            Async.beginWork(100, updateProdAttrWork);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return productAttr;
    }

    /**
     * 删称 Trapper 类型 ITEM
     *
     * @param productAttr 属性
     * @return String
     */
    public void deleteTrapperItem(ProductAttr productAttr) {

        //检查是否有属性依赖
        int count = new QProductAttribute().depAttrId.in(productAttr.getAttrIds()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_DEPTED);
        }

        //检查属性是否被告警规则引入
        List<Long> attrIds = new QProductAttribute().select(QProductAttribute.alias().attrId).templateId.in(productAttr.getAttrIds()).findSingleAttributeList();
        count = new QProductEventExpression().productAttrId.in(attrIds).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_EVENT_HASDEPTED);
        }

        List<String> zbxIds = new QProductAttribute().select(QProductAttribute.alias().zbxId).attrId.in(productAttr.getAttrIds()).findSingleAttributeList();
        //删除zbx item
        if (ToolUtil.isNotEmpty(zbxIds)) {
            List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemInfo(zbxIds.toString(), null), ZbxItemInfo.class);
            if (ToolUtil.isNotEmpty(itemInfos)) {
                zbxItem.deleteTrapperItem(itemInfos.parallelStream().map(ZbxItemInfo::getItemid).collect(Collectors.toList()));
            }
        }

        //删除 属性
        new QProductAttribute().attrId.in(productAttr.getAttrIds()).delete();

        //删除 设备 继承的属性
        new QProductAttribute().templateId.in(productAttr.getAttrIds()).delete();
    }
}
