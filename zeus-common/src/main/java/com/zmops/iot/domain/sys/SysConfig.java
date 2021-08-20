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
 * @author nantian created at 2021/8/1 20:51
 */
@EqualsAndHashCode(callSuper = false)
@Table(name = "sys_config")
@Entity
@Data
public class SysConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = IdTypeConsts.ID_SNOW)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 属性编码标识
     */
    private String code;

    /**
     * 是否是字典中的值
     */
    private String dictFlag;

    /**
     * 字典类型的编码
     */
    private Long dictTypeId;

    /**
     * 属性值，如果是字典中的类型，则为dict的code
     */
    private String value;

    /**
     * 备注
     */
    private String remark;

}
