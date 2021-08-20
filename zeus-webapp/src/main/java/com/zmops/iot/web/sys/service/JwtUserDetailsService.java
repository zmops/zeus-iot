package com.zmops.iot.web.sys.service;


import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.web.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情信息获取
 *
 * @author fengshuonan
 */
@Service("jwtUserDetailsService")
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthService authService;

    @Override
    public LoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return authService.user(username);
    }
}
