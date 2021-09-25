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
package com.zmops.iot.web.sys.factory;

import cn.hutool.core.bean.BeanUtil;
import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.constant.state.ManagerStatus;
import com.zmops.iot.web.sys.dto.UserDto;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户创建工厂
 *
 * @author fengshuonan
 */
public class UserFactory {

    /**
     * 根据请求创建实体
     */
    public static SysUser createUser(UserDto userDto, String md5Password, String salt) {
        if (userDto == null) {
            return null;
        } else {
            SysUser user = new SysUser();
            BeanUtils.copyProperties(userDto, user);

            user.setStatus(ManagerStatus.OK.getCode());
            user.setPassword(md5Password);
            user.setSalt(salt);
            return user;
        }
    }

    /**
     * 更新user
     */
    public static SysUser editUser(UserDto newUser, SysUser oldUser) {
        if (newUser == null || oldUser == null) {
            return oldUser;
        } else {
            if (ToolUtil.isNotEmpty(newUser.getName())) {
                oldUser.setName(newUser.getName());
            }
            if (ToolUtil.isNotEmpty(newUser.getEmail())) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (ToolUtil.isNotEmpty(newUser.getUserGroupId())) {
                oldUser.setUserGroupId(newUser.getUserGroupId());
            }
            if (ToolUtil.isNotEmpty(newUser.getRoleId())) {
                oldUser.setRoleId(newUser.getRoleId());
            }
            if (ToolUtil.isNotEmpty(newUser.getPhone())) {
                oldUser.setPhone(newUser.getPhone());
            }
            if (ToolUtil.isNotEmpty(newUser.getStatus())) {
                oldUser.setStatus(newUser.getStatus());
            }
            return oldUser;
        }
    }

    /**
     * 过滤不安全字段并转化为map
     */
    public static Map<String, Object> removeUnSafeFields(SysUser user) {
        if (user == null) {
            return new HashMap<>();
        } else {
            Map<String, Object> map = BeanUtil.beanToMap(user);
            map.remove("password");
            map.remove("salt");
            return map;
        }
    }

    /**
     * 通过用户表的信息创建一个登录用户
     */
    public static LoginUser createLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();

        if (user == null) {
            return loginUser;
        }

        loginUser.setId(user.getUserId());
        loginUser.setAccount(user.getAccount());
        loginUser.setUserGroupId(user.getUserGroupId());
//        loginUser.setDeptId(user.getDeptId());
//        loginUser.setDeptName(ConstantFactory.me().getDeptName(user.getDeptId()));
        loginUser.setName(user.getName());
        loginUser.setEmail(user.getEmail());
        loginUser.setZbxToken(user.getZbxToken());
//        loginUser.setAvatar("/api/system/preview/" + user.getAvatar());

        return loginUser;
    }

    /**
     * 判断用户是否是从oauth2登录过来的
     */
    public static boolean oauth2Flag() {
        String account = LoginContextHolder.getContext().getUser().getAccount();
        if (account.startsWith(ConstantsContext.getOAuth2UserPrefix())) {
            return true;
        } else {
            return false;
        }
    }
}
