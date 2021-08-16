package com.zmops.iot.web.device.dto;

import com.zmops.iot.domain.BaseDto;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class DeviceDto implements BaseDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long deviceId;

    @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private String name;

    @NotNull(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private Long productId;

    @NotNull(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private List<Long> deviceGroupIds;

    private String status;

    private String type;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @CachedValue(type = DicType.SysUserName)
    private Long createUser;

    @CachedValue(type = DicType.SysUserName)
    private Long updateUser;

    private Long oldProductId;

    private String zbxId;

}
