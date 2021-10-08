package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.Tag;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductBasicInfo;
import com.zmops.iot.web.product.dto.ProductDto;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.zeus.driver.service.ZbxTemplate;
import com.zmops.zeus.driver.service.ZbxValueMap;
import io.ebean.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.zmops.iot.web.init.DeviceSatusScriptInit.GLOBAL_HOST_GROUP_CODE;

/**
 * @author nantian created at 2021/8/3 20:41
 */

@Service
public class ProductService {

    @Autowired
    private ZbxTemplate zbxTemplate;

    @Autowired
    private ZbxValueMap zbxValueMap;

    /**
     * 产品列表
     */
    public List<Product> prodList(ProductBasicInfo prodBasicInfo) {
        QProduct qProduct = new QProduct();
        if (ToolUtil.isNotEmpty(prodBasicInfo.getTag())) {
            QTag qTag = new QTag().select(QTag.Alias.sid).templateId.isNull();
            qTag.tag.contains(prodBasicInfo.getTag());
            if (ToolUtil.isNotEmpty(prodBasicInfo.getTagVal())) {
                qTag.value.contains(prodBasicInfo.getTagVal());
            }
            List<Long> sids = qTag.findSingleAttributeList();
            if (ToolUtil.isNotEmpty(sids)) {
                qProduct.productId.in(sids);
            }
        }
        if (null != prodBasicInfo.getGroupId()) {
            qProduct.groupId.eq(prodBasicInfo.getGroupId());
        }
        if (ToolUtil.isNotEmpty(prodBasicInfo.getProdType())) {
            qProduct.type.eq(prodBasicInfo.getProdType());
        }
        if (ToolUtil.isNotEmpty(prodBasicInfo.getProdName())) {
            qProduct.name.contains(prodBasicInfo.getProdName());
        }
        return qProduct.orderBy(" create_time desc").findList();
    }

    /**
     * 产品分页列表
     */
    public Pager<ProductDto> getProductByPage(ProductBasicInfo prodBasicInfo) {
        QProduct qProduct = new QProduct();

        if (null != prodBasicInfo.getGroupId()) {
            qProduct.groupId.eq(prodBasicInfo.getGroupId());
        }
        if (ToolUtil.isNotEmpty(prodBasicInfo.getProdType())) {
            qProduct.type.eq(prodBasicInfo.getProdType());
        }
        if (ToolUtil.isNotEmpty(prodBasicInfo.getProdName())) {
            qProduct.name.contains(prodBasicInfo.getProdName());
        }

        List<ProductDto> list = qProduct.setFirstRow((prodBasicInfo.getPage() - 1) * prodBasicInfo.getMaxRow())
                .setMaxRows(prodBasicInfo.getMaxRow()).orderBy(" create_time desc").asDto(ProductDto.class).findList();

        if (ToolUtil.isNotEmpty(list)) {
            List<Long>      productIds = list.parallelStream().map(ProductDto::getProductId).collect(Collectors.toList());
            List<Device>    deviceList = new QDevice().select(QDevice.alias().productId, QDevice.alias().totalCount).productId.in(productIds).findList();
            Map<Long, Long> map        = deviceList.parallelStream().collect(Collectors.toMap(Device::getProductId, Device::getTotalCount));
            for (ProductDto productDto : list) {
                productDto.setDeviceNum(Optional.ofNullable(map.get(productDto.getProductId())).orElse(0L));
            }
        }

        return new Pager<>(list, qProduct.findCount());
    }


    /**
     * 创建 zabbix 模板
     *
     * @param templateName zabbix 模板名称
     * @return
     */
    public String zbxTemplateCreate(String templateName) {
        // 全局分组
        String hostGroupId = ConstantsContext.getConstntsMap().get(GLOBAL_HOST_GROUP_CODE).toString();
        return zbxTemplate.templateCreate(templateName, hostGroupId);
    }


    /**
     * 根据模板ID 删除模板
     *
     * @param templdateId 模板ID
     * @return
     */
    public String zbxTemplateDelete(String templdateId) {

        JSONArray jsonArray = JSONObject.parseArray(zbxTemplate.templateGet(templdateId));
        if (jsonArray.size() > 0) {
            zbxTemplate.templateDelete(templdateId);
        }
        return templdateId;
    }

    /**
     * 保存 产品
     *
     * @param zbxId         模板ID
     * @param productId     产品ID
     * @param prodBasicInfo 产品基本信息
     * @return
     */
    public Product createProduct(String zbxId, Long productId, ProductBasicInfo prodBasicInfo) {
        Product product = buildPord(prodBasicInfo);
        product.setProductId(productId);
        product.setZbxId(zbxId);

        DB.save(product);

        return product;
    }


    /**
     * 更新模板标签，不可以重名，覆盖更新
     *
     * @param tag 标签列表
     * @return
     */
    public String updateTemplateTags(String templateId, ProductTag tag) {

        Map<String, String> tagMap = tag.getProductTag().stream()
                .collect(Collectors.toMap(ProductTag.Tag::getTag, ProductTag.Tag::getValue, (k1, k2) -> k2));

        return zbxTemplate.templateTagUpdate(templateId, tagMap);
    }


    /**
     * 创建产品 值映射
     *
     * @param hostid       产品模板ID
     * @param valueMapName 名称
     * @param valueMaps    KV
     * @return
     */
    public String valueMapCreate(String hostid, String valueMapName, Map<String, String> valueMaps) {
        return zbxValueMap.valueMapCreate(hostid, valueMapName, valueMaps);
    }

    /**
     * 修改 产品值映射
     *
     * @param hostid       产品模板ID
     * @param valueMapName 名称
     * @param valueMaps    KV
     * @param valueMapId   映射ID
     * @return
     */
    public String valueMapUpdate(String hostid, String valueMapName, Map<String, String> valueMaps, String valueMapId) {
        return zbxValueMap.valueMapUpdate(hostid, valueMapName, valueMaps, valueMapId);
    }

    /**
     * 删除值映射
     *
     * @param valueMapId 值映射ID
     * @return String
     */
    public String valueMapDelete(String valueMapId) {
        return zbxValueMap.valueMapDelete(valueMapId);
    }


    /**
     * 修改 产品 基本信息
     *
     * @param prodBasicInfo 基本信息
     * @return
     */
    public Product updateProduct(ProductBasicInfo prodBasicInfo) {

        Product product = buildPord(prodBasicInfo);
        product.setProductId(prodBasicInfo.getProductId());

        DB.update(product);

        return product;
    }

    private static Product buildPord(ProductBasicInfo prodBasicInfo) {
        Product product = new Product();

        product.setProductCode(prodBasicInfo.getProdCode());
        product.setName(prodBasicInfo.getProdName());
        product.setManufacturer(prodBasicInfo.getManufacturer());
        product.setModel(prodBasicInfo.getModel());
        product.setRemark(prodBasicInfo.getRemark());
        product.setType(prodBasicInfo.getProdType());
        product.setGroupId(prodBasicInfo.getGroupId());

        return product;
    }

    /**
     * 产品详情
     *
     * @param prodId
     * @return
     */
    public ProductDto prodDetail(Long prodId) {
        ProductDto prod = new QProduct().productId.eq(prodId).asDto(ProductDto.class).findOne();

        return prod;
    }

    /**
     * 产品标签列表
     *
     * @param prodId
     * @return
     */
    public List<Tag> prodTagList(String prodId) {
        QTag tag = QTag.alias();
        return new QTag().select(tag.id, tag.sid, tag.tag, tag.value).sid.eq(prodId).orderBy(" create_time desc").findList();
    }

    /**
     * 产品值映射
     *
     * @param prodId
     * @return
     */
    public JSONArray valueMapList(Long prodId) {
        JSONArray zbxTemplateInfo = getZbxTemplateInfo(prodId);
        if (zbxTemplateInfo.size() == 0) {
            return new JSONArray();
        }
        //查询ZBX valueMap
        return JSONObject.parseArray(zbxTemplateInfo.getJSONObject(0).getString("valuemaps"));
    }

    /**
     * 根据产品ID 取zbx template信息
     *
     * @param prodId
     * @return
     */
    private JSONArray getZbxTemplateInfo(Long prodId) {
        String zbxId = new QProduct().select(QProduct.alias().zbxId).productId.eq(prodId).findSingleAttribute();
        if (null == zbxId) {
            return new JSONArray();
        }
        return JSONObject.parseArray(zbxTemplate.templateDetail(zbxId));
    }


}
