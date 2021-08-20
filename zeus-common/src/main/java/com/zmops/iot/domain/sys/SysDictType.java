package com.zmops.iot.domain.sys;

import com.zmops.iot.constant.IdTypeConsts;
import com.zmops.iot.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 字典类型表
 * </p>
 */
@EqualsAndHashCode(callSuper = false)
@Table(name = "sys_dict_type")
@Entity
@Data
public class SysDictType extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型id
     */
    @Id
    @GeneratedValue(generator = IdTypeConsts.ID_SNOW)
    private Long dictTypeId;

    /**
     * 字典类型编码
     */
    private String code;

    /**
     * 字典类型名称
     */
    private String name;

    /**
     * 字典描述
     */
    private String remark;

    /**
     * 是否是系统字典，Y-是，N-否
     */
    private String systemFlag = "N";

    /**
     * 状态
     */
    private String status;

    /**
     * 排序
     */
    private Integer sort;

}
