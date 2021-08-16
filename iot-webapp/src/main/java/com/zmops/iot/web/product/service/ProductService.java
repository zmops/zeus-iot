package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.constant.ConstantsContext;
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
import io.ebean.DtoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
        QProduct      qProduct = new QProduct();
        StringBuilder sql      = generateBasSql();
        sql.append(" where 1=1 ");
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

    private StringBuilder generateBasSql() {
        return new StringBuilder("SELECT " +
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
                " pt.name group_name," +
                " dt.name prodTypeName " +
                "FROM" +
                " product p" +
                " LEFT JOIN product_type pt ON pt.ID = p.group_id " +
                " LEFT JOIN ( SELECT code, NAME FROM sys_dict WHERE dict_type_id = 1160532886713155586 ) dt ON dt.code = p.type ");
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
     * @param valueMapId    映射ID
     * @return
     */
    public String valueMapUpdate(String hostid, String valueMapName, Map<String, String> valueMaps, String valueMapId) {
        return zbxValueMap.valueMapUpdate(hostid, valueMapName, valueMaps,valueMapId);
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


    public ProductDto prodDetail(Long prodId) {
        StringBuilder sql = generateBasSql();
        sql.append(" where product_id = :prodId");
        ProductDto prod = DB.findDto(ProductDto.class, sql.toString()).setParameter("prodId", prodId).findOne();

        //查询ZBX 主机宏
//        List<Macr> tags      = JSONObject.parseArray(jsonArray.getJSONObject(0).getString("tags"), ProductTag.Tag.class);

        return prod;
    }

    public JSONArray prodTagList(Long prodId) {

        JSONArray zbxTemplateInfo = getZbxTemplateInfo(prodId);

        //查询ZBX tag
        return JSONObject.parseArray(zbxTemplateInfo.getJSONObject(0).getString("tags"));
    }

    public JSONArray valueMapList(Long prodId) {
        JSONArray zbxTemplateInfo = getZbxTemplateInfo(prodId);

        //查询ZBX valueMap
        return JSONObject.parseArray(zbxTemplateInfo.getJSONObject(0).getString("valuemaps"));
    }

    private JSONArray getZbxTemplateInfo(Long prodId) {
        String zbxId = new QProduct().select(QProduct.alias().zbxId).productId.eq(prodId).findSingleAttribute();
        if (null == zbxId) {
            return new JSONArray();
        }
        return JSONObject.parseArray(zbxTemplate.templateDetail(zbxId));
    }


}
