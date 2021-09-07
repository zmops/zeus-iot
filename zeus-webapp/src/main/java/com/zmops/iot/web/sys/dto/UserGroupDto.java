/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zmops.iot.web.sys.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.cache.filter.CachedValue;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户组传输bean
 *
 * @author yefei
 */
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class UserGroupDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long userGroupId;

    @NotBlank
    private String groupName;

    @CachedValue(value = "STATUS")
    private String status;

    private String remark;

    private List<Long> deviceGroupIds;

    //列表返回的设备组ID
    private String groupIds;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    Long createUser;

    Long updateUser;
}
