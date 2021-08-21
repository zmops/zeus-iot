package com.zmops.iot.web.sys.service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zmops.iot.domain.sys.SysDict;
import com.zmops.iot.domain.sys.SysDictType;
import com.zmops.iot.domain.sys.query.QSysDict;
import com.zmops.iot.domain.sys.query.QSysDictType;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.sys.dto.param.DictParam;
import io.ebean.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 基础字典 服务实现类
 * </p>
 */
@Service
@Order(Integer.MAX_VALUE)
public class DictService implements CommandLineRunner {

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 新增
     */
    public SysDict add(DictParam param) {

        //判断是否已经存在同编码或同名称字典
        int count = new QSysDict()
                .dictTypeId.eq(param.getDictTypeId()).or()
                .code.ieq(param.getCode())
                .name.ieq(param.getName()).findCount();

        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DICT_EXISTED);
        }

        //判断字典是否存在
        count = new QSysDictType().dictTypeId.eq(param.getDictTypeId()).findCount();
        if (count <= 0) {
            throw new ServiceException(BizExceptionEnum.ERROR_CODE_EMPTY);
        }

        SysDict entity = getEntity(param);

        //设置状态
        entity.setStatus(CommonStatus.ENABLE.getCode());

        DB.save(entity);
        updateDictionaries();
        return entity;
    }


    /**
     * 删除
     */
    public void delete(DictParam param) {
        String systemFlag = new QSysDictType().select(QSysDictType.alias().systemFlag).dictTypeId.eq(param.getDictTypeId()).findSingleAttribute();
        if ("Y".equals(systemFlag)) {
            throw new ServiceException(BizExceptionEnum.SYSTEM_DICT_CANNOT_DELETE);
        }
        new QSysDict().dictId.in(param.getDictIds()).delete();
        updateDictionaries();
    }

    /**
     * 更新
     */
    public SysDict update(DictParam param) {
        SysDict oldEntity = new QSysDict().dictId.eq(param.getDictId()).findOne();
        if (null == oldEntity) {
            throw new ServiceException(BizExceptionEnum.DICT_NOT_EXIST);
        }

        //判断编码是否重复
        int count = new QSysDict().or().code.ieq(param.getCode()).name.ieq(param.getName()).endOr()
                .dictId.ne(param.getDictId()).dictTypeId.eq(param.getDictTypeId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DICT_EXISTED);
        }
        //判断字典是否存在
        count = new QSysDictType().dictTypeId.eq(param.getDictTypeId()).findCount();
        if (count <= 0) {
            throw new ServiceException(BizExceptionEnum.ERROR_CODE_EMPTY);
        }

        SysDict newEntity = getEntity(param);
        ToolUtil.copyProperties(newEntity, oldEntity);


        DB.update(newEntity);
        updateDictionaries();
        return newEntity;
    }

    /**
     * 查询字典列表，通过字典ID
     */
    public List<SysDict> listDicts(Long dictTypeId) {
        return new QSysDict().dictTypeId.eq(dictTypeId).orderBy(" sort desc").findList();
    }

    /**
     * 查询字典列表，通过字典编码
     */
    public List<SysDict> listDicts(String dictTypeCode) {
        Long dictTypeId = new QSysDictType().select(QSysDictType.alias().dictTypeId).code.eq(dictTypeCode).findSingleAttribute();
        return listDicts(dictTypeId);
    }

    /**
     * 分组查询字典列表，通过字典编码
     */
    public Map<String, List<SysDict>> groupDictByCode(String dictTypeCode) {
        Long          dictTypeId = new QSysDictType().select(QSysDictType.alias().dictTypeId).code.eq(dictTypeCode).findSingleAttribute();
        List<SysDict> sysDicts   = listDicts(dictTypeId);
        if (ToolUtil.isEmpty(sysDicts)) {
            return new HashMap<>();
        }
        return sysDicts.parallelStream().collect(Collectors.groupingBy(SysDict::getGroups));
    }

    private void updateDictionaries() {
        List<SysDictType>             dictTypes        = new QSysDictType().findList();
        Map<Long, String>             map              = dictTypes.parallelStream().collect(Collectors.toMap(SysDictType::getDictTypeId, SysDictType::getCode));
        List<Long>                    collect          = dictTypes.stream().map(SysDictType::getDictTypeId).collect(Collectors.toList());
        List<SysDict>                 dictList         = new QSysDict().dictTypeId.in(collect).findList();
        Table<String, String, String> dictionaryValues = HashBasedTable.create();
        for (SysDict dict : dictList) {
            dictionaryValues.put(map.get(dict.getDictTypeId()), dict.getCode(), dict.getName());
        }
        DefinitionsUtil.updateDictionaries(dictionaryValues);
    }

    @Override
    public void run(String... args) throws Exception {
        updateDictionaries();
    }

    //    /**
//     * 查询分页数据，Specification模式
//     *
//     */
//    public LayuiPageInfo findPageBySpec(DictParam param) {
//        QueryWrapper<Dict> objectQueryWrapper = new QueryWrapper<>();
//        objectQueryWrapper.eq("dict_type_id", param.getDictTypeId());
//
//        if (ToolUtil.isNotEmpty(param.getCondition())) {
//            objectQueryWrapper.and(i -> i.eq("code", param.getCondition()).or().eq("name", param.getCondition()));
//        }
//
//        objectQueryWrapper.orderByAsc("sort");
//
//        List<Dict> list = this.list(objectQueryWrapper);
//
//        //去除根节点为0的
//        if (list.size() > 0) {
//            for (Dict dict : list) {
//                if (dict.getParentId() != null && dict.getParentId().equals(0L)) {
//                    dict.setParentId(null);
//                }
//            }
//        }
//
//        LayuiPageInfo result = new LayuiPageInfo();
//        result.setData(list);
//
//        return result;
//    }
//
//    /**
//     * 获取字典的树形列表（ztree结构）
//     *
//     */
//    public List<ZTreeNode> dictTreeList(Long dictTypeId, Long dictId) {
//        if (dictTypeId == null) {
//            throw new RequestEmptyException();
//        }
//
//        List<ZTreeNode> tree = this.baseMapper.dictTree(dictTypeId);
//
//        //获取dict的所有子节点
//        List<Long> subIds = getSubIds(dictId);
//
//        //如果传了dictId，则在返回结果里去掉
//        List<ZTreeNode> resultTree = new ArrayList<>();
//        for (ZTreeNode zTreeNode : tree) {
//
//            //如果dictId等于树节点的某个id则去除
//            if (ToolUtil.isNotEmpty(dictId) && dictId.equals(zTreeNode.getId())) {
//                continue;
//            }
//            if (subIds.contains(zTreeNode.getId())) {
//                continue;
//            }
//            resultTree.add(zTreeNode);
//        }
//
//        resultTree.add(ZTreeNode.createParent());
//
//        return resultTree;
//    }
//
//    /**
//     * 查看dict的详情
//     *
//     */
//    public DictResult dictDetail(Long dictId) {
//        if (ToolUtil.isEmpty(dictId)) {
//            throw new RequestEmptyException();
//        }
//
//        DictResult dictResult = new DictResult();
//
//        //查询字典
//        Dict detail = this.getById(dictId);
//        if (detail == null) {
//            throw new RequestEmptyException();
//        }
//
//        //查询父级字典
//        if (ToolUtil.isNotEmpty(detail.getParentId())) {
//            Long parentId = detail.getParentId();
//            Dict dictType = this.getById(parentId);
//            if (dictType != null) {
//                dictResult.setParentName(dictType.getName());
//            } else {
//                dictResult.setParentName("无父级");
//            }
//        }
//
//        ToolUtil.copyProperties(detail, dictResult);
//
//        return dictResult;
//    }
//

    //
//    /**
//     * 查询字典列表，通过字典类型code
//     *
//     */
//    public List<Dict> listDictsByCode(String dictTypeCode) {
//
//        QueryWrapper<DictType> wrapper = new QueryWrapper<>();
//        wrapper.eq("code", dictTypeCode);
//
//        DictType one = this.dictTypeService.getOne(wrapper);
//        return listDicts(one.getDictTypeId());
//    }
//
//    /**
//     * 查询字典列表，通过字典类型code
//     *
//     */
//    public List<Map<String, Object>> getDictsByCodes(List<String> dictCodes) {
//
//        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
//        wrapper.in("code", dictCodes).orderByAsc("sort");
//
//        ArrayList<Map<String, Object>> results = new ArrayList<>();
//
//        //转成map
//        List<Dict> list = this.list(wrapper);
//        for (Dict dict : list) {
//            Map<String, Object> map = BeanUtil.beanToMap(dict);
//            results.add(map);
//        }
//
//        return results;
//    }
//
//    private Serializable getKey(DictParam param) {
//        return param.getDictId();
//    }
//
//    private Page getPageContext() {
//        return LayuiPageFactory.defaultPage();
//    }
//
//    private Dict getOldEntity(DictParam param) {
//        return this.getById(getKey(param));
//    }
//
    private SysDict getEntity(DictParam param) {
        SysDict entity = new SysDict();
        ToolUtil.copyProperties(param, entity);
        return entity;
    }

//
//    private List<Long> getSubIds(Long dictId) {
//
//        ArrayList<Long> longs = new ArrayList<>();
//
//        if (ToolUtil.isEmpty(dictId)) {
//            return longs;
//        } else {
//            List<Dict> list = this.baseMapper.likeParentIds(dictId);
//            for (Dict dict : list) {
//                longs.add(dict.getDictId());
//            }
//            return longs;
//        }
//    }
//
//    private void dictSetPids(Dict param) {
//        if (param.getParentId().equals(0L)) {
//            param.setParentIds("[0]");
//        } else {
//            //获取父级的pids
//            Long parentId = param.getParentId();
//            Dict parent = this.getById(parentId);
//            if (parent == null) {
//                param.setParentIds("[0]");
//            } else {
//                param.setParentIds(parent.getParentIds() + "," + "[" + parentId + "]");
//            }
//        }
//    }

}
