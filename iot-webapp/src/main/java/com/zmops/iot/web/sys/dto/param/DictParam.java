package com.zmops.iot.web.sys.dto.param;

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 基础字典
 * </p>
 *
 * @author stylefeng
 */
@Data
public class DictParam implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 字典id
     */
    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Get.class})
    private Long dictId;

    /**
     * 所属字典类型的id
     */
    @NotNull(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private Long dictTypeId;

    /**
     * 所属字典类型的code
     */
    private List<String> dictTypeCodes;

    /**
     * 字典编码
     */
    @NotBlank(groups = {BaseEntity.Create.class})
    private String code;

    /**
     * 字典名称
     */
    @NotBlank(groups = {BaseEntity.Create.class})
    private String name;


    /**
     * 状态（字典）
     */
    private String status;


    /**
     * 字典的描述
     */
    private String remark;

    /**
     * 查询条件
     */
    private String condition;

    private Integer sort;

    @NotNull(groups = {BaseEntity.MassRemove.class})
    private List<Long> dictIds;

    private String dictTypeCode;

    private Integer page = 1;
    private Integer size = 10;


}
