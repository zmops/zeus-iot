package com.zmops.iot.core.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author nantian created at 2021/7/29 18:16
 */

@Data
@AllArgsConstructor
public class UserRole implements GrantedAuthority {

    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }
}
