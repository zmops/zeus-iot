package com.zmops.iot.model.cache.filter;

import lombok.Getter;

/**
 * 指明序列化给前段时间需要增加的字段的类型
 */
public enum DicType {

    SysUserName(false),
    SysRole,
    ProdType,
    Tenant,
    Device,
    Dictionary;


    @Getter
    private boolean nullable;

    DicType() {
        this.nullable = true;
    }

    DicType(boolean nullable) {
        this.nullable = nullable;
    }
}
