package com.zmops.iot.web.product.service;

import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.iot.web.product.dto.ProductBasicInfo;
import com.zmops.iot.web.product.dto.ProductDto;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.zeus.driver.entity.ZbxProcessingStep;
import com.zmops.zeus.driver.service.ZbxItem;
import com.zmops.zeus.driver.service.ZbxTemplate;
import com.zmops.zeus.driver.service.ZbxValueMap;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private ZbxItem zbxItem;

    /**
     * 产品列表
     */
    public List<Product> prodList(ProductBasicInfo prodBasicInfo) {
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
        return qProduct.findList();
    }

    /**
     * 产品分页列表
     */
    public Pager<ProductDto> getProductByPage(ProductBasicInfo prodBasicInfo) {
        QProduct qProduct = new QProduct();
        StringBuilder sql = new StringBuilder("SELECT " +
                " p.product_id," +
                " p.group_id," +
                " p.name," +
                " p.type," +
                " p.manufacturer," +
                " p.model," +
                " p.remark," +
                " p.create_time," +
                " p.create_user," +
                " p.update_time," +
                " p.update_user," +
                " p.product_code," +
                " pt.name group_name " +
                "FROM" +
                " product p" +
                " LEFT JOIN product_type pt ON pt.ID = p.group_id where 1=1 ");
        if (null != prodBasicInfo.getGroupId()) {
            sql.append(" and p.group_id = :groupId");
        }
        if (ToolUtil.isNotEmpty(prodBasicInfo.getProdType())) {
            sql.append(" and p.type = :type");
        }
        if (ToolUtil.isNotEmpty(prodBasicInfo.getProdName())) {
            sql.append(" and p.name like :name");
        }
        DtoQuery<ProductDto> query = DB.findDto(ProductDto.class, sql.toString());


        if (null != prodBasicInfo.getGroupId()) {
            query.setParameter("groupId", prodBasicInfo.getGroupId());
            qProduct.groupId.eq(prodBasicInfo.getGroupId());
        }
        if (ToolUtil.isNotEmpty(prodBasicInfo.getProdType())) {
            query.setParameter("type", prodBasicInfo.getProdType());
            qProduct.type.eq(prodBasicInfo.getProdType());
        }
        if (ToolUtil.isNotEmpty(prodBasicInfo.getProdName())) {
            query.setParameter("name", "%" + prodBasicInfo.getProdName() + "%");
            qProduct.name.contains(prodBasicInfo.getProdName());
        }

        List<ProductDto> list = query.setFirstRow((prodBasicInfo.getPage() - 1) * prodBasicInfo.getMaxRow())
                .setMaxRows(prodBasicInfo.getMaxRow()).findList();

        return new Pager<>(list, qProduct.findCount());
    }

    /**
     * 创建 产品属性
     *
     * @param productAttr 产品属性DTO
     */
    public void createProductAttr(ProductAttr productAttr) {
        ProductAttribute productAttribute = buildProdAttribute(productAttr);
        productAttribute.save();
    }


    /**
     * 创建 Trapper 类型 ITEM
     *
     * @param productAttr 属性
     * @return String
     */
    public String createTrapperItem(ProductAttr productAttr) {

        String itemName = productAttr.getAttrId() + "";

        Product prod = new QProduct().productId.eq(productAttr.getProductId()).findOne();
        if (null == prod) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        Integer hostId = prod.getZbxId();

        List<ZbxProcessingStep> processingSteps = new ArrayList<>();
        productAttr.getProcessStepList().forEach(i -> {
            ZbxProcessingStep step = new ZbxProcessingStep();

            step.setType(i.getType());
            step.setParams(i.getParams());

            processingSteps.add(step);
        });

        return zbxItem.createTrapperItem(itemName, productAttr.getKey(),
                hostId, productAttr.getValueType(), productAttr.getUints(), processingSteps);
    }


    private ProductAttribute buildProdAttribute(ProductAttr productAttr) {
        ProductAttribute prodAttribute = new ProductAttribute();

        prodAttribute.setProductId(productAttr.getProductId());
        prodAttribute.setName(productAttr.getAttrName());
        prodAttribute.setKey(productAttr.getKey());
        prodAttribute.setSource(productAttr.getSource());
        prodAttribute.setUints(productAttr.getUints());
        prodAttribute.setRemark(productAttr.getRemark());

        prodAttribute.setAttrId(productAttr.getAttrId());
        prodAttribute.setZbxId(productAttr.getZbxId());

        return prodAttribute;
    }


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


    public ProductDto prodDetail(Long prodId) {

        return null;
    }
}
