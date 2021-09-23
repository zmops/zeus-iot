package com.zmops.iot.web.product.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yefei
 **/
@Data
public class EventParm extends BaseQueryParam {

    private String eventRuleName;

    @NotBlank
    private String prodId;

    @NotBlank
    private String classify;
}
