package com.zmops.iot.web.alarm.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class AlarmParam extends BaseQueryParam {
    private String deviceId;

    private long clock;
}
