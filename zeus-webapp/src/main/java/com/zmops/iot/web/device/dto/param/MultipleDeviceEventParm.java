package com.zmops.iot.web.device.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
public class MultipleDeviceEventParm extends BaseQueryParam {

    private String eventRuleName;

    private List<String> deviceIds;

    private List<Long> deviceGrpIds;

}
