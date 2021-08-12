package com.zmops.iot.web.product.service;

import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.iot.web.product.dto.param.ProductAttrParam;
import com.zmops.zeus.driver.entity.ZbxProcessingStep;
import com.zmops.zeus.driver.service.ZbxItem;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class ProductModelService {

    @Autowired
    private ZbxItem zbxItem;

    /**
     * 产品属性分页列表
     *
     * @param productAttr
     * @return
     */
    public Pager<ProductAttr> prodModelAttributeList(ProductAttrParam productAttr) {
        QProductAttribute qProductAttribute = new QProductAttribute();
        StringBuilder sql = new StringBuilder("select attr.attr_id, attr.name attrName, attr.key, attr.units, attr.source, attr.remark, attr.product_id, attr.create_user, " +
                "attr.create_time, attr.update_user, attr.update_time, attr.zbx_id, attr.value_type," +
                "u.name unit_name,v.name value_type_name,s.name source_name from product_attribute attr");
        sql.append(" left join (select code,name from sys_dict where dict_type_id=1160532886713155589) u on u.code = attr.units ");
        sql.append(" left join (select code,name from sys_dict where dict_type_id=1160532886713155590) v on v.code = attr.value_type ");
        sql.append(" left join (select code,name from sys_dict where dict_type_id=1160532886713155588) s on s.code = attr.source where 1=1 ");

        if (null != productAttr.getProdId()) {
            sql.append(" and attr.product_id = :productId");
        }
        if (ToolUtil.isNotEmpty(productAttr.getAttrName())) {
            sql.append(" and attr.name like :name");
        }
        if (ToolUtil.isNotEmpty(productAttr.getKey())) {
            sql.append(" and attr.key like :key");
        }
        sql.append(" order by attr.create_time desc ");

        DtoQuery<ProductAttr> dto = DB.findDto(ProductAttr.class, sql.toString());

        if (null != productAttr.getProdId()) {
            dto.setParameter("productId", productAttr.getProdId());
            qProductAttribute.productId.eq(productAttr.getProdId());
        }
        if (ToolUtil.isNotEmpty(productAttr.getAttrName())) {
            dto.setParameter("name", "%" + productAttr.getAttrName() + "%");
            qProductAttribute.name.contains(productAttr.getAttrName());
        }
        if (ToolUtil.isNotEmpty(productAttr.getKey())) {
            dto.setParameter("key", "%" + productAttr.getKey() + "%");
            qProductAttribute.key.contains(productAttr.getKey());
        }

        List<ProductAttr> pagedList = dto.setFirstRow((productAttr.getPage() - 1) * productAttr.getMaxRow()).setMaxRows(productAttr.getMaxRow()).findList();
        int               count     = qProductAttribute.findCount();
        return new Pager<>(pagedList, count);
    }

    /**
     * 产品属性列表
     *
     * @param productAttr
     * @return
     */
    public List<ProductAttribute> list(ProductAttrParam productAttr) {
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
        return qProductAttribute.findList();
    }

    /**
     * 创建 产品属性
     *
     * @param productAttr 产品属性DTO
     */
    public void createProductAttr(ProductAttr productAttr) {
        ProductAttribute productAttribute = new ProductAttribute();
        buildProdAttribute(productAttribute, productAttr);
        productAttribute.save();
    }

    private ProductAttribute buildProdAttribute(ProductAttribute prodAttribute, ProductAttr productAttr) {
        prodAttribute.setProductId(productAttr.getProductId());
        prodAttribute.setName(productAttr.getAttrName());
        prodAttribute.setKey(productAttr.getKey());
        prodAttribute.setSource(productAttr.getSource());
        prodAttribute.setUints(productAttr.getUnits());
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

        Product prod = new QProduct().productId.eq(productAttr.getProductId()).findOne();
        if (null == prod) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        Integer hostId = prod.getZbxId();

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
                hostId, productAttr.getValueType(), productAttr.getUnits(), processingSteps, productAttr.getValuemapid(), tagMap);
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
        Product prod = new QProduct().productId.eq(productAttr.getProductId()).findOne();
        if (null == prod) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        Integer                 hostId          = prod.getZbxId();
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
                hostId, productAttr.getValueType(), productAttr.getUnits(), processingSteps, productAttr.getValuemapid(), tagMap);

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

        List<Integer> zbxIds = new QProductAttribute().select(QProductAttribute.alias().zbxId).attrId.in(productAttr.getAttrIds()).findSingleAttributeList();

        zbxItem.deleteTrapperItem(zbxIds);

        new QProductAttribute().attrId.in(productAttr.getAttrIds()).delete();
    }

}
