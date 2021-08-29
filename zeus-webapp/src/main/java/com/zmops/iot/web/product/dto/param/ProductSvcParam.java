package com.zmops.iot.web.product.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class ProductSvcParam extends BaseQueryParam {

    private String name;

    private String mark;
}
