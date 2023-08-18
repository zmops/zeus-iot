package com.zmops.iot.web.device.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
public class DeviceParams extends BaseQueryParam {

    private String name;
    private String deviceId;

    private List<Long> productIds;

    private List<Long> deviceGroupIds;

    private List<Long> prodTypes;

    private String tag;

    private String tagVal;

}
