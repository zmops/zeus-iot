package com.zmops.iot.web.sys.service;

import com.zmops.iot.domain.sys.DictType;
import com.zmops.iot.domain.sys.query.QDict;
import com.zmops.iot.domain.sys.query.QDictType;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.sys.dto.param.DictTypeParam;
import io.ebean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 字典类型表 服务实现类
 * </p>
 */
@Service
public class DictTypeService {

    @Autowired
    private DictService dictService;

    /**
     * 新增
     */
    public DictType add(DictTypeParam param) {

        //判断是否已经存在同编码或同名称字典
        Expression or    = Expr.or(Expr.eq("code", param.getCode()), Expr.eq("name", param.getName()));
        int        count = DB.find(DictType.class).where().add(or).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DICT_EXISTED);
        }

        DictType entity = getEntity(param);

        //设置状态
        entity.setStatus(CommonStatus.ENABLE.getCode());

        DB.save(entity);
        return entity;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(DictTypeParam param) {

        //删除字典类型
        new QDictType().dictTypeId.in(param.getDictTypeIds()).delete();

        //删除字典
        new QDict().dictTypeId.in(param.getDictTypeIds()).delete();
    }

    /**
     * 更新
     */
    public DictType update(DictTypeParam param) {
        //判断编码是否重复
        Expression or    = Expr.or(Expr.eq("code", param.getCode()), Expr.eq("name", param.getName()));
        int        count = DB.find(DictType.class).where().add(or).add(Expr.ne("dict_type_id", param.getDictTypeId())).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DICT_EXISTED);
        }

        DictType oldEntity = new QDictType().dictTypeId.eq(param.getDictTypeId()).findOne();
        DictType newEntity = getEntity(param);
        ToolUtil.copyProperties(newEntity, oldEntity);

        DB.update(newEntity);
        return newEntity;
    }


    /**
     * 查询分页数据
     */
    public Pager findPageBySpec(DictTypeParam param) {
        ExpressionList<DictType> where = DB.find(DictType.class).where();
        if (ToolUtil.isNotEmpty(param.getCondition())) {
            Expression or = Expr.or(Expr.eq("code", param.getCode()), Expr.eq("name", param.getName()));
            where.add(or);
        }
//        if (ToolUtil.isNotEmpty(param.getStatus())) {
//            objectQueryWrapper.and(i -> i.eq("status", param.getStatus()));
//        }
//        if (ToolUtil.isNotEmpty(param.getSystemFlag())) {
//            objectQueryWrapper.and(i -> i.eq("system_flag", param.getSystemFlag()));
//        }
        where.orderBy().desc("sort");
        where.setFirstRow((param.getPage() - 1) * param.getMaxRow()).setMaxRows(param.getMaxRow());
        PagedList<DictType> pagedList = where.findPagedList();
        return new Pager<>(pagedList.getList(), pagedList.getTotalCount());
    }

    private DictType getEntity(DictTypeParam param) {
        DictType entity = new DictType();
        ToolUtil.copyProperties(param, entity);
        return entity;
    }

}
