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

import com.zmops.iot.domain.sys.SysUser;
import lombok.Data;

/**
 * 用户传输bean
 *
 * @author stylefeng
 */
@Data
public class LoginUserDto {

    private Long userId;

    private String account;

    private String name;

    private String token;

    public static LoginUserDto buildLoginUser(SysUser sysUser, String token) {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setUserId(sysUser.getUserId());
        loginUserDto.setAccount(sysUser.getAccount());
        loginUserDto.setName(sysUser.getName());
        loginUserDto.setToken(token);
        return loginUserDto;
    }
}
