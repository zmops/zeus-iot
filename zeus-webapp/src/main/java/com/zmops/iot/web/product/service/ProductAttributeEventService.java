package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.async.executor.Async;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttributeEvent;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.iot.web.product.dto.ProductAttrDto;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.iot.web.product.dto.param.ProductAttrParam;
import com.zmops.iot.web.product.service.work.AsyncAttrEventZbxIdWorker;
import com.zmops.iot.web.product.service.work.SaveProdAttrEventWorker;
import com.zmops.iot.web.product.service.work.UpdateAttributeWorker;
import com.zmops.zeus.driver.entity.ZbxProcessingStep;
import com.zmops.zeus.driver.service.ZbxItem;
import io.ebean.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class ProductAttributeEventService {

    @Autowired
    private ZbxItem zbxItem;

    @Autowired
    SaveProdAttrEventWorker saveProdAttrEventWorker;

    @Autowired
    AsyncAttrEventZbxIdWorker asyncAttrEventZbxIdWorker;

    @Autowired
    UpdateAttributeWorker updateProdAttrWorker;

    /**
     * 产品属性分页列表
     *
     * @param productAttr
     * @return
     */
    public Pager<ProductAttrDto> prodAttributeEventList(ProductAttrParam productAttr) {
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

        List<ProductAttrDto> pagedList = qProductAttribute.setFirstRow((productAttr.getPage() - 1) * productAttr.getMaxRow()).setMaxRows(productAttr.getMaxRow()).asDto(ProductAttrDto.class).findList();
        int                  count     = qProductAttribute.findCount();
        return new Pager<>(pagedList, count);
    }

    /**
     * 产品属性列表
     *
     * @param productAttr
     * @return
     */
    public List<ProductAttrDto> list(ProductAttrParam productAttr) {
        QProductAttribute qProductAttribute = new QProductAttribute();
        if (null != productAttr.getProdId()) {
            qProductAttribute.productId.eq(productAttr.getProdId());
        }
        if (ToolUtil.isNotEmpty(productAttr.getAttrName())) {
            qProductAttribute.name.contains(productAttr.getAttrName());
        }
        if (ToolUtil.isNotEmpty(productAttr.getKey())) {
            qProductAttribute.key.contains(productAttr.getKey());
        }
        return qProductAttribute.orderBy(" create_time desc").asDto(ProductAttrDto.class).findList();
    }

    /**
     * 产品属性详情
     *
     * @param attrId
     * @return
     */
    public ProductAttrDto detail(Long attrId) {
        ProductAttrDto attr = new QProductAttribute().attrId.eq(attrId).asDto(ProductAttrDto.class).findOne();

        if (null == attr.getZbxId()) {
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
        JSONArray                        jsonArray       = JSONObject.parseArray(preprocessing);
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
        ProductAttributeEvent productAttributeEvent = new ProductAttributeEvent();
        buildProdAttribute(productAttributeEvent, productAttr);
        productAttributeEvent.setZbxId(zbxId);
        productAttributeEvent.save();


        WorkerWrapper<ProductAttr, Boolean> asyncAttrEventZbxIdWork = WorkerWrapper.<ProductAttr, Boolean>builder()
                .worker(asyncAttrEventZbxIdWorker).param(productAttr).build();
        WorkerWrapper<ProductAttr, Boolean> saveProdAttrEventWork = WorkerWrapper.<ProductAttr, Boolean>builder()
                .worker(saveProdAttrEventWorker).param(productAttr).nextOf(asyncAttrEventZbxIdWork).build();

        try {
            Async.work(10000, saveProdAttrEventWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ProductAttributeEvent buildProdAttribute(ProductAttributeEvent prodAttribute, ProductAttr productAttr) {
        prodAttribute.setProductId(productAttr.getProductId());
        prodAttribute.setName(productAttr.getAttrName());
        prodAttribute.setKey(productAttr.getKey());
        prodAttribute.setUnits(productAttr.getUnits());
        prodAttribute.setRemark(productAttr.getRemark());
        prodAttribute.setValueType(productAttr.getValueType());
        prodAttribute.setAttrId(productAttr.getAttrId());
        prodAttribute.setEventLevel(productAttr.getEventLevel());
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
                hostId, "2", null, productAttr.getValueType(), productAttr.getUnits(), processingSteps, productAttr.getValuemapid(), tagMap);
    }

    /**
     * 修改 Trapper 类型 ITEM
     *
     * @param productAttr 属性
     * @return String
     */
    public ProductAttr updateTrapperItem(ProductAttr productAttr) {
        ProductAttributeEvent productAttributeEvent = new QProductAttributeEvent().attrId.eq(productAttr.getAttrId()).findOne();
        buildProdAttribute(productAttributeEvent, productAttr);
        Product prod = new QProduct().productId.eq(Long.parseLong(productAttr.getProductId())).findOne();
        if (null == prod) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        String                  hostId          = prod.getZbxId();
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


        zbxItem.updateTrapperItem(productAttributeEvent.getZbxId(), productAttr.getAttrId() + "", productAttr.getKey(),
                hostId, "2", null, productAttr.getValueType(), productAttr.getUnits(), processingSteps, productAttr.getValuemapid(), tagMap);

        DB.update(productAttributeEvent);

        WorkerWrapper<ProductAttr, Boolean> updateProdAttrWork = WorkerWrapper.<ProductAttr, Boolean>builder().worker(updateProdAttrWorker).param(productAttr).build();

        try {
            Async.work(100, updateProdAttrWork).awaitFinish();
        } catch (InterruptedException e) {
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

        List<String> zbxIds = new QProductAttribute().select(QProductAttribute.alias().zbxId).attrId.in(productAttr.getAttrIds()).findSingleAttributeList();
        //删除zbx item
        zbxItem.deleteTrapperItem(zbxIds);

        //删除 属性
        new QProductAttribute().attrId.in(productAttr.getAttrIds()).delete();

        //删除 设备 继承的属性
        new QProductAttribute().templateId.in(productAttr.getAttrIds()).delete();
    }
}
