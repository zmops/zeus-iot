package com.zmops.iot.web.device.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class DeviceLogParam extends BaseQueryParam {
    private String logType;
    private String deviceId;
    private String content;
    private Long timeFrom;
    private Long timeTill;

}
