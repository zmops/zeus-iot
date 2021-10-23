package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.enums.CommonTimeUnit;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.analyse.service.LatestService;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.iot.web.product.dto.ProductAttrDto;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.iot.web.product.dto.param.ProductAttrParam;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.entity.ZbxProcessingStep;
import com.zmops.zeus.driver.service.ZbxItem;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 设备物模型服务
 **/
@Service
public class DeviceModelService {

    @Autowired
    private ZbxItem zbxItem;

    @Autowired
    LatestService latestService;

    /**
     * 设备属性分页列表
     *
     * @param productAttr
     * @return
     */
    public Pager<ProductAttrDto> prodModelAttributeList(ProductAttrParam productAttr) {
        QProductAttribute qProductAttribute = new QProductAttribute();

        qProductAttribute.productId.eq(productAttr.getProdId());

        if (ToolUtil.isNotEmpty(productAttr.getAttrName())) {
            qProductAttribute.name.contains(productAttr.getAttrName());
        }
        if (ToolUtil.isNotEmpty(productAttr.getKey())) {
            qProductAttribute.key.contains(productAttr.getKey());
        }

        List<ProductAttrDto> pagedList = qProductAttribute.setFirstRow((productAttr.getPage() - 1) * productAttr.getMaxRow())
                .setMaxRows(productAttr.getMaxRow()).orderBy(" create_time desc").asDto(ProductAttrDto.class).findList();

        if (ToolUtil.isEmpty(pagedList)) {
            return new Pager<>();
        }
        //查询最新数据
        List<Long> attrIds = pagedList.parallelStream().map(ProductAttrDto::getAttrId).collect(Collectors.toList());
        List<LatestDto> latestDtos = latestService.qeuryLatest(productAttr.getProdId(), attrIds);
        Map<Long, LatestDto> map = latestDtos.parallelStream().distinct().collect(Collectors.toMap(LatestDto::getAttrId, o -> o, (a, b) -> b));

        //查询zbx item 信息
        List<String> zbxIds = pagedList.parallelStream().map(ProductAttrDto::getZbxId).collect(Collectors.toList());

        String itemInfo = zbxItem.getItemInfo(zbxIds.toString(), null);
        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(itemInfo, ZbxItemInfo.class);
        Map<String, String> errorMap = itemInfos.parallelStream().collect(Collectors.toMap(ZbxItemInfo::getItemid, o -> Optional.ofNullable(o.getError()).orElse("")));

        pagedList.forEach(productAttrDto -> {
            if (null != map.get(productAttrDto.getAttrId())) {
                productAttrDto.setClock(map.get(productAttrDto.getAttrId()).getClock());
                productAttrDto.setValue(map.get(productAttrDto.getAttrId()).getValue());
                productAttrDto.setOriginalValue(map.get(productAttrDto.getAttrId()).getOriginalValue());
                productAttrDto.setDelayName(productAttrDto.getDelay() + CommonTimeUnit.getDescription(productAttrDto.getUnit()));
            }
            if (null != errorMap.get(productAttrDto.getZbxId())) {
                productAttrDto.setError(errorMap.get(productAttrDto.getZbxId()));
            }
        });

        int count = qProductAttribute.findCount();
        return new Pager<>(pagedList, count);
    }

    /**
     * 设备属性列表
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
//        return qProductAttribute.asDto(ProductAttrDto.class).findList();
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
     * 设备属性详情
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
        if (itemInfo.size() == 0) {
            return attr;
        }
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
     * 创建 设备物模型
     *
     * @param productAttr 产品属性DTO
     */
    public void createProductAttr(ProductAttr productAttr, String zbxId) {
        ProductAttribute productAttribute = new ProductAttribute();
        buildProdAttribute(productAttribute, productAttr);
        productAttribute.setZbxId(zbxId);
        productAttribute.save();
    }

    private ProductAttribute buildProdAttribute(ProductAttribute prodAttribute, ProductAttr productAttr) {
        if (null == prodAttribute.getTemplateId()) {
            prodAttribute.setName(productAttr.getAttrName());
            prodAttribute.setKey(productAttr.getKey());
            prodAttribute.setSource(productAttr.getSource());
            prodAttribute.setUnits(productAttr.getUnits());
            prodAttribute.setDepAttrId(productAttr.getDepAttrId());
            prodAttribute.setValueType(productAttr.getValueType());
            prodAttribute.setDelay(productAttr.getDelay());
            prodAttribute.setUnit(productAttr.getUnit());
            prodAttribute.setValuemapid(productAttr.getValuemapid());
        }
        prodAttribute.setProductId(productAttr.getProductId());
        prodAttribute.setRemark(productAttr.getRemark());
        prodAttribute.setAttrId(productAttr.getAttrId());

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

        Device device = new QDevice().deviceId.eq(productAttr.getProductId()).findOne();
        if (null == device) {
            throw new ServiceException(BizExceptionEnum.DEVICE_NOT_EXISTS);
        }
        String hostId = device.getZbxId();

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
        Device device = new QDevice().deviceId.eq(productAttr.getProductId()).findOne();
        if (null == device) {
            throw new ServiceException(BizExceptionEnum.DEVICE_NOT_EXISTS);
        }
        String hostId = device.getZbxId();

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
        if (ToolUtil.isNotEmpty(zbxIds)) {
            List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemInfo(zbxIds.toString(), null), ZbxItemInfo.class);
            if (ToolUtil.isNotEmpty(itemInfos)) {
                zbxItem.deleteTrapperItem(itemInfos.parallelStream().map(ZbxItemInfo::getItemid).collect(Collectors.toList()));
            }
        }

        //删除 属性
        new QProductAttribute().attrId.in(productAttr.getAttrIds()).delete();

    }
}
