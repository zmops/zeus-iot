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

import com.zmops.iot.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户传输bean
 *
 * @author stylefeng
 */
@Data
public class UserDto {

    @NotNull(groups = BaseEntity.Update.class)
    private Long userId;

    @NotBlank
    private String account;

    @NotBlank
    private String password;

    @NotBlank
    private String name;


    @NotBlank
    private String email;

    private String phone;

    @NotNull
    private Long userGroupId;

    private String status;

    private Long roleId;

    private String roleName;
    private String userGroupName;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;
}
