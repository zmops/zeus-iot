package com.zmops.iot.web.sys.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.sys.dto.param.DictParam;
import com.zmops.iot.web.sys.service.DictService;
import com.zmops.iot.web.sys.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 基础字典控制器
 */
@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @Autowired
    private DictTypeService dictTypeService;


    /**
     * 新增字典项
     */
    @RequestMapping("/create")
    @BussinessLog("新增字典项")
    public ResponseData addItem(@Validated(BaseEntity.Create.class) @RequestBody DictParam dictParam) {
        return ResponseData.success(dictService.add(dictParam));
    }

    /**
     * 编辑字典项
     */
    @RequestMapping("/update")
    @BussinessLog("编辑字典项")
    public ResponseData editItem(@Validated(BaseEntity.Update.class) @RequestBody DictParam dictParam) {
        return ResponseData.success(dictService.update(dictParam));
    }

    /**
     * 删除字典项
     *
     * @author stylefeng
     */
    @RequestMapping("/delete")
    @BussinessLog("删除字典项")
    public ResponseData delete(@Validated(BaseEntity.MassRemove.class) @RequestBody DictParam dictParam) {
        dictService.delete(dictParam);
        return ResponseData.success();
    }

    /**
     * 获取某个字典类型下的所有字典
     */
    @ResponseBody
    @RequestMapping("/listDicts")
    public ResponseData listDicts(@RequestParam("dictTypeId") Long dictTypeId) {
        return ResponseData.success(dictService.listDicts(dictTypeId));
    }

    /**
     * 获取某个字典类型下的所有字典
     */
    @ResponseBody
    @RequestMapping("/listDictByCode")
    public ResponseData listDictByCode(@RequestParam("dictTypeCode") String dictTypeCode) {
        return ResponseData.success(dictService.listDicts(dictTypeCode));
    }

    /**
     * 获取某个字典类型下的所有字典
     */
    @ResponseBody
    @RequestMapping("/groupDictByCode")
    public ResponseData groupDictByCode(@RequestParam("dictTypeCode") String dictTypeCode) {
        return ResponseData.success(dictService.groupDictByCode(dictTypeCode));
    }

    /**
     * 查看详情接口
     *
     */
//    @RequestMapping("/detail")
//    @ResponseBody
//    public ResponseData detail(DictParam dictParam) {
//        DictResult dictResult = this.dictService.dictDetail(dictParam.getDictId());
//        return ResponseData.success(dictResult);
//    }

    /**
     * 查询列表
     *
     */
//    @ResponseBody
//    @RequestMapping("/list")
//    public LayuiPageInfo list(DictParam dictParam) {
//        return this.dictService.findPageBySpec(dictParam);
//    }


    /**
     * 获取某个字典类型下的所有字典
     *
     */
//    @ResponseBody
//    @RequestMapping("/listDictsByCode")
//    public ResponseData listDictsByCode(@RequestParam("dictTypeCode") String dictTypeCode) {
//        List<Dict> dicts = this.dictService.listDictsByCode(dictTypeCode);
//        return new SuccessResponseData(dicts);
//    }

    /**
     * 获取某个类型下字典树的列表，ztree格式
     *
     */
//    @RequestMapping(value = "/ztree")
//    @ResponseBody
//    public List<ZTreeNode> ztree(@RequestParam("dictTypeId") Long dictTypeId, @RequestParam(value = "dictId", required = false) Long dictId) {
//        return this.dictService.dictTreeList(dictTypeId, dictId);
//    }

}


