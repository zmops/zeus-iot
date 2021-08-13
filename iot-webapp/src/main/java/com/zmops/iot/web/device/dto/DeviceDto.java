package com.zmops.iot.web.device.dto;

import com.zmops.iot.domain.BaseDto;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@Data
public class DeviceDto implements BaseDto {

    private Long deviceId;

    private String name;

    private Long productId;

    private Long deviceGroupId;

    private String status;

    private String type;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName)
    private Long createUser;

    @CachedValue(type = DicType.SysUserName)
    private Long updateUser;

}
