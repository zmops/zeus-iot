package com.zmops.iot.web.product.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yefei
 **/
@Data
public class ProductSvcParam extends BaseQueryParam {

    private String name;

    private String mark;

    @NotBlank
    private String prodId;
}
