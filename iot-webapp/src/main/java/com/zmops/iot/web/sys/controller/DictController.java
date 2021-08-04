package com.zmops.iot.web.sys.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.sys.Dict;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.sys.dto.param.DictParam;
import com.zmops.iot.web.sys.service.DictService;
import com.zmops.iot.web.sys.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * 基础字典控制器
 *
 */
@Controller
@RequestMapping("/dict")
public class DictController  {

    @Autowired
    private DictService dictService;

    @Autowired
    private DictTypeService dictTypeService;



    /**
     * 新增接口
     *
     */
    @RequestMapping("/addItem")
    @ResponseBody
    public ResponseData addItem(@Validated(BaseEntity.Create.class) @RequestBody DictParam dictParam) {
        return ResponseData.success(dictService.add(dictParam));
    }

    /**
     * 编辑接口
     *
     */
    @RequestMapping("/editItem")
    @ResponseBody
    public ResponseData editItem(@Validated(BaseEntity.Update.class) @RequestBody DictParam dictParam) {
        return ResponseData.success(dictService.update(dictParam));
    }

    /**
     * 删除接口
     *
     * @author stylefeng
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseData delete(@Validated(BaseEntity.MassRemove.class) @RequestBody DictParam dictParam) {
        dictService.delete(dictParam);
        return ResponseData.success();
    }

    /**
     * 获取某个字典类型下的所有字典
     *
     */
    @ResponseBody
    @RequestMapping("/listDicts")
    public ResponseData listDicts(@RequestParam("dictTypeId") Long dictTypeId) {
        return ResponseData.success(dictService.listDicts(dictTypeId));
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


