package com.zmops.iot.web.sys.dto.param;


import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 字典类型表
 * </p>
 *
 * @author stylefeng
 */
@Data
public class DictTypeParam extends BaseQueryParam {


    /**
     * 字典类型id
     */
    @NotNull(groups = {BaseEntity.Update.class, BaseEntity.Get.class})
    private Long dictTypeId;

    /**
     * 是否是系统字典，Y-是，N-否
     */
    @NotBlank(groups = {BaseEntity.Create.class})
    private String systemFlag;

    /**
     * 字典类型编码
     */
    @NotBlank(groups = {BaseEntity.Create.class})
    private String code;

    /**
     * 字典类型名称
     */
    @NotBlank(groups = {BaseEntity.Create.class})
    private String name;

    /**
     * 字典描述
     */
    private String remark;

    /**
     * 状态(字典)
     */
    private String status;

    /**
     * 查询条件
     */
    private String condition;

    @NotEmpty(groups = {BaseEntity.MassRemove.class})
    private List<Long> dictTypeIds;

}
