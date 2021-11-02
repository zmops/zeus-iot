package com.zmops.iot.web.device.dto.param;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 设备组参数
 *
 * @author yefei
 */
@Data
public class DeviceGroupParam extends BaseQueryParam {

    @NotNull(groups = BaseEntity.Update.class)
    private Long deviceGroupId;

    @NotBlank(groups = {BaseEntity.Update.class, BaseEntity.Create.class})
    private String name;

    private String remark;

    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> deviceGroupIds;
}
