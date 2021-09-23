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
import com.zmops.iot.model.cache.filter.DicType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户传输bean
 *
 * @author stylefeng
 */
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class UserDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long userId;

    @NotBlank(groups = {BaseEntity.Create.class})
    private String account;

    @NotBlank(groups = {BaseEntity.Create.class})
    private String password;

    @NotBlank(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private String name;

    private String email;

    private String phone;

    @NotNull(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private Long userGroupId;

    @CachedValue(value = "STATUS")
    private String status;

    @NotNull(groups = {BaseEntity.Create.class, BaseEntity.Update.class})
    private Long roleId;

    private String roleName;
    private String userGroupName;
    @CachedValue(type = DicType.SysUserName)
    private Long createUser;
    private String createTime;
    @CachedValue(type = DicType.SysUserName)
    private Long updateUser;
    private String updateTime;
}
