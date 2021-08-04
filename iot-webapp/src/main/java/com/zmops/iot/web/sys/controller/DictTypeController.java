package com.zmops.iot.web.sys.controller;

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
     * 新增接口
     */
    @RequestMapping("/addItem")
    public ResponseData addItem(@Validated(BaseEntity.Create.class) @RequestBody DictTypeParam dictTypeParam) {
        return ResponseData.success(dictTypeService.add(dictTypeParam));
    }

    /**
     * 编辑接口
     */
    @RequestMapping("/editItem")
    public ResponseData editItem(@Validated(BaseEntity.Update.class) @RequestBody DictTypeParam dictTypeParam) {
        return ResponseData.success(dictTypeService.update(dictTypeParam));
    }

    /**
     * 删除接口
     */
    @RequestMapping("/delete")
    @ResponseBody
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
//     * @author stylefeng
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


