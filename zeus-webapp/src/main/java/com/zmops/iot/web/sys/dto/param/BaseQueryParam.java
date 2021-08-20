package com.zmops.iot.web.sys.dto.param;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class BaseQueryParam {
    private int page   = 1;
    private int maxRow = 10;
}
