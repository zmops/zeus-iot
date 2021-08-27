package com.zmops.iot.web.analyse.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class LatestParam  extends BaseQueryParam{
    @NotNull(message = "请选择一个设备再查询")
    private Long deviceId;

    private List<Long> attrIds;


}
