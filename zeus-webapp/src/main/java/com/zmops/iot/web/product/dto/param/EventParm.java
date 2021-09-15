package com.zmops.iot.web.product.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class EventParm extends BaseQueryParam {

    private String eventRuleName;

    private String prodId;
}
