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
 * 基础字典
 * </p>
 *
 */
@EqualsAndHashCode(callSuper = false)
@Table(name = "sys_dict")
@Entity
@Data
public class Dict extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典id
     */
    @Id
    @GeneratedValue(generator = IdTypeConsts.ID_SNOW)
    private Long dictId;

    /**
     * 所属字典类型的id
     */
    private Long dictTypeId;

    /**
     * 字典编码
     */
    private String code;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 状态
     */
    private String status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 字典的描述
     */
    private String description;
}
