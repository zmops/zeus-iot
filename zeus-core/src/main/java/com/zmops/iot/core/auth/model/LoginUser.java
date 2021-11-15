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
package com.zmops.iot.core.auth.model;


import com.zmops.iot.util.ToolUtil;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 当前登录用户信息
 *
 * @author fengshuonan
 */
@Data
public class LoginUser implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    public LoginUser() {
        super();
    }

    public LoginUser(Long id, String token) {
        super();
        this.id = id;
        this.zbxToken = token;
    }

    /**
     * 用户主键ID
     */
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户组
     */
    private Long userGroupId;

    /**
     * 角色集
     */
    private List<Long> roleList;

    /**
     * 角色名称集
     */
    private List<String> roleNames;

    /**
     * 角色备注（code）
     */
    private List<String> roleTips;

    /**
     * 系统标识集合
     */
    private List<Map<String, Object>> systemTypes;

    /**
     * 拥有的权限
     */
    private Set<String> permissions;

    /**
     * zabbix token
     *
     * @return
     */
    private String zbxToken;


    @Override
    public List<UserRole> getAuthorities() {
        ArrayList<UserRole> grantedAuthorities = new ArrayList<>();
        if (ToolUtil.isNotEmpty(this.roleNames)) {
            for (String roleName : this.roleNames) {
                grantedAuthorities.add(new UserRole(roleName));
            }
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.account;
    }

    @Override
    public boolean isAccountNonExpired() {
        //能生成loginUser就是jwt解析成功，没锁定
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //能生成loginUser就是jwt解析成功，没锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //能生成loginUser就是jwt解析成功，没锁定
        return true;
    }

    @Override
    public boolean isEnabled() {
        //能生成loginUser就是jwt解析成功，没锁定
        return true;
    }
}
