package com.zmops.iot.web.sys.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.sys.dto.param.DictTypeParam;
import com.zmops.iot.web.sys.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 字典类型表控制器
 */
@RestController
@RequestMapping("/dictType")
public class DictTypeController {

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 新增字典
     */
    @RequestMapping("/create")
    @BussinessLog("新增字典")
    public ResponseData addItem(@Validated(BaseEntity.Create.class) @RequestBody DictTypeParam dictTypeParam) {
        return ResponseData.success(dictTypeService.add(dictTypeParam));
    }

    /**
     * 编辑字典
     */
    @RequestMapping("/update")
    @BussinessLog("编辑字典")
    public ResponseData editItem(@Validated(BaseEntity.Update.class) @RequestBody DictTypeParam dictTypeParam) {
        return ResponseData.success(dictTypeService.update(dictTypeParam));
    }

    /**
     * 删除字典
     */
    @RequestMapping("/delete")
    @BussinessLog("删除字典")
    public ResponseData delete(@Validated(BaseEntity.MassRemove.class) @RequestBody DictTypeParam dictTypeParam) {
        this.dictTypeService.delete(dictTypeParam);
        return ResponseData.success();
    }


    /**
     * 查询列表
     */
    @PostMapping("/getDictTypeByPage")
    public Pager list(@RequestBody DictTypeParam dictTypeParam) {
        return dictTypeService.findPageBySpec(dictTypeParam);
    }


    //    /**
//     * 查看详情接口
//     *
//     */
//    @RequestMapping("/detail")
//    @ResponseBody
//    public ResponseData detail(DictTypeParam dictTypeParam) {
//        DictType detail = this.dictTypeService.getById(dictTypeParam.getDictTypeId());
//        return ResponseData.success(detail);
//    }

    /**
     * 查询所有字典
     *
     */
//    @ResponseBody
//    @RequestMapping("/listTypes")
//    public ResponseData listTypes() {
//
//        QueryWrapper<DictType> objectQueryWrapper = new QueryWrapper<>();
//        objectQueryWrapper.select("dict_type_id", "code", "name");
//
//        List<DictType> list = this.dictTypeService.list(objectQueryWrapper);
//        return new SuccessResponseData(list);
//    }

}


