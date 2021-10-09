package com.zmops.iot.web.product.service;

import com.zmops.iot.core.tree.DefaultTreeBuildFactory;
import com.zmops.iot.domain.product.ProductType;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.domain.product.query.QProductType;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.node.TreeNode;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.param.ProductTypeParam;
import io.ebean.DB;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 产品分类管理
 **/
@Service
public class ProductTypeService implements CommandLineRunner {

    /**
     * 产品分类树
     */
    public List<TreeNode> tree() {
        String sql = "select id,pid pId,name,pids from product_type";
        List<TreeNode> list = DB.findDto(TreeNode.class, sql).findList();
        DefaultTreeBuildFactory<TreeNode> treeBuildFactory = new DefaultTreeBuildFactory<>();
        treeBuildFactory.setRootParentId("0");
        return treeBuildFactory.doTreeBuild(list);
    }

    /**
     * 新增分类
     *
     * @param productTypeParam
     * @return
     */
    public ProductType create(ProductTypeParam productTypeParam) {

        int count = new QProductType().name.eq(productTypeParam.getName())
                .pid.eq(productTypeParam.getPid())
                .findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_TYPE_EXIST);
        }
        ProductType productType = new ProductType();
        ToolUtil.copyProperties(productTypeParam, productType);
        setPids(productType);
        DB.save(productType);
        updateProductType();
        return productType;
    }

    /**
     * 新增分类
     *
     * @param productTypeParam
     * @return
     */
    public Object update(ProductTypeParam productTypeParam) {
        int oldProTypeCount = new QProductType().id.eq(productTypeParam.getId()).findCount();
        if (oldProTypeCount <= 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_TYPE_NOT_EXIST);
        }
        int count = new QProductType().name.eq(productTypeParam.getName())
                .pid.eq(productTypeParam.getPid())
                .id.ne(productTypeParam.getId())
                .findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_TYPE_EXIST);
        }

        ProductType productType = new ProductType();
        ToolUtil.copyProperties(productTypeParam, productType);
        setPids(productType);
        DB.update(productType);
        updateProductType();
        return productType;
    }

    /**
     * 新增分类
     *
     * @param productTypeParam
     * @return
     */
    public void delete(ProductTypeParam productTypeParam) {
        int count = new QProduct().groupId.in(productTypeParam.getIds()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_TYPE_HAS_BIND);
        }

        new QProductType().id.in(productTypeParam.getIds()).delete();
        updateProductType();
    }


    /**
     * 设置父级ids
     */
    private void setPids(ProductType productType) {
        if (productType.getPid().equals(0L)) {
            productType.setPids("[0],");
        } else {
            Long pid = productType.getPid();
            ProductType temp = new QProductType().id.eq(pid).findOne();
            if (null == temp) {
                throw new ServiceException(BizExceptionEnum.PRODUCT_TYPE_PID_NOT_EXIST);
            }
            if (null != productType.getId() && productType.getId().equals(productType.getPid())) {
                throw new ServiceException(BizExceptionEnum.PRODUCT_TYPE_PID_ERR);
            }
            if (null != productType.getId() && temp.getPids().contains("[" + productType.getId() + "]")) {
                throw new ServiceException(BizExceptionEnum.PRODUCT_TYPE_PID_ERR);
            }
            String pids = temp.getPids();
            productType.setPids(pids + "[" + pid + "],");
        }
    }

    private void updateProductType() {
        List<ProductType> list = new QProductType().findList();
        if (ToolUtil.isEmpty(list)) {
            return;
        }
        DefinitionsUtil.updateProductType(list.parallelStream().collect(Collectors.toMap(ProductType::getId, ProductType::getName)));
    }

    @Override
    public void run(String... args) throws Exception {
        updateProductType();
    }
}
