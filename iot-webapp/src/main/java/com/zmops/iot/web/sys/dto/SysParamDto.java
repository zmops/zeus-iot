package com.zmops.iot.web.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author nantian created at 2021/8/1 20:51
 */
@Data
public class SysParamDto {

    @NotNull
    private List<SysParam> sysParamList;

    @Data
    public static class SysParam {
        private Long id;

        /**
         * 名称
         */
        @NotNull
        private String name;

        /**
         * 属性值，如果是字典中的类型，则为dict的code
         */
        @NotNull
        private String value;

        /**
         * 备注
         */
        private String remark;
    }
}
