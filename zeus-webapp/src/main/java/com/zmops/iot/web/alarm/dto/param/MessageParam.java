package com.zmops.iot.web.alarm.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import java.util.List;

/**
 * @author yefei
 **/
@Data
public class MessageParam extends BaseQueryParam {
    private Integer readed;

    private List<Integer> ids;
}
