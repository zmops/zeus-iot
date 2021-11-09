package com.zmops.iot.web.device.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class MultipleDeviceEventParm extends BaseQueryParam {

    private String eventRuleName;

}
