package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.web.product.dto.ProductBasicInfo;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.zeus.driver.service.ZbxTemplate;
import com.zmops.zeus.driver.service.ZbxValueMap;
import io.ebean.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

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
     * 创建 zabbix 模板
     *
     * @param templateName zabbix 模板名称
     * @return
     */
    public String zbxTemplateCreate(String templateName) {
        return zbxTemplate.templateCreate(templateName, "27"); //TODO
    }


    /**
     * 根据模板ID 删除模板
     *
     * @param templdateId 模板ID
     * @return
     */
    public String zbxTemplateDelete(String templdateId) {
        return zbxTemplate.templateDelete(templdateId);
    }

    /**
     * 保存 产品
     *
     * @param zbxId         模板ID
     * @param productId     产品ID
     * @param prodBasicInfo 产品基本信息
     * @return
     */
    public Product createProduct(Integer zbxId, Long productId, ProductBasicInfo prodBasicInfo) {
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
    public String updateTemplateTags(Integer templateId, ProductTag tag) {

        Map<String, String> tagMap = tag.getProductTag().stream()
                .collect(Collectors.toMap(ProductTag.Tag::getKey, ProductTag.Tag::getValue, (k1, k2) -> k2));

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
}
