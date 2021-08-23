package com.zmops.iot.web.analyse.dto.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class HistoryParam {

    @NotNull(message = "请选择一个设备再查询")
    private Long deviceId;

    private List<Long> attrIds;

    private Long timeFrom;

    private Long timeTill;
}
