package com.zmops.iot.web.sys.service;

import com.zmops.iot.domain.sys.SysDictType;
import com.zmops.iot.domain.sys.query.QSysDict;
import com.zmops.iot.domain.sys.query.QSysDictType;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.sys.dto.param.DictTypeParam;
import io.ebean.DB;
import io.ebean.PagedList;
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
    public SysDictType add(DictTypeParam param) {

        //判断是否已经存在同编码或同名称字典
        int count = new QSysDictType().or().code.eq(param.getCode()).name.eq(param.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DICT_EXISTED);
        }

        SysDictType entity = getEntity(param);

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
        String systemFlag = new QSysDictType().select(QSysDictType.alias().systemFlag).dictTypeId.eq(param.getDictTypeId()).findSingleAttribute();
        if ("Y".equals(systemFlag)) {
            throw new ServiceException(BizExceptionEnum.SYSTEM_DICT_CANNOT_DELETE);
        }
        //删除字典类型
        new QSysDictType().dictTypeId.in(param.getDictTypeIds()).delete();

        //删除字典
        new QSysDict().dictTypeId.in(param.getDictTypeIds()).delete();
    }

    /**
     * 更新
     */
    public SysDictType update(DictTypeParam param) {
        SysDictType oldEntity = new QSysDictType().dictTypeId.eq(param.getDictTypeId()).findOne();
        if (null == oldEntity) {
            throw new ServiceException(BizExceptionEnum.DICT_NOT_EXIST);
        }
        //判断编码是否重复
        int count = new QSysDictType().dictTypeId.ne(param.getDictTypeId())
                .or().code.eq(param.getCode()).name.eq(param.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DICT_EXISTED);
        }

        SysDictType newEntity = getEntity(param);
        ToolUtil.copyProperties(newEntity, oldEntity);

        DB.update(newEntity);
        return newEntity;
    }


    /**
     * 查询分页数据
     */
    public Pager findPageBySpec(DictTypeParam param) {
        QSysDictType qSysDictType = new QSysDictType();
        if (ToolUtil.isNotEmpty(param.getCondition())) {
            qSysDictType.or().code.eq(param.getCondition()).name.eq(param.getCondition());
        }
//        if (ToolUtil.isNotEmpty(param.getStatus())) {
//            objectQueryWrapper.and(i -> i.eq("status", param.getStatus()));
//        }
//        if (ToolUtil.isNotEmpty(param.getSystemFlag())) {
//            objectQueryWrapper.and(i -> i.eq("system_flag", param.getSystemFlag()));
//        }
        qSysDictType.orderBy().dictTypeId.desc();
        qSysDictType.setFirstRow((param.getPage() - 1) * param.getMaxRow()).setMaxRows(param.getMaxRow());
        PagedList<SysDictType> pagedList = qSysDictType.findPagedList();
        return new Pager<>(pagedList.getList(), pagedList.getTotalCount());
    }

    private SysDictType getEntity(DictTypeParam param) {
        SysDictType entity = new SysDictType();
        ToolUtil.copyProperties(param, entity);
        return entity;
    }

}
