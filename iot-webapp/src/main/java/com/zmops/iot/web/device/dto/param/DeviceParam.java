package com.zmops.iot.web.device.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
public class DeviceParam extends BaseQueryParam {

    private String name;

    private Long productId;

    private Long deviceGroupId;

    private String prodType;

    private String tag;

    private String tagVal;

}
