package com.zmops.iot.web.auth;

import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.core.auth.context.LoginContext;
import com.zmops.iot.core.auth.exception.AuthException;
import com.zmops.iot.core.auth.exception.enums.AuthExceptionEnum;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.core.auth.util.TokenUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 用户登录上下文
 *
 * @author fengshuonan
 */
@Component
public class LoginContextSpringSecutiryImpl implements LoginContext {

    @Override
    public LoginUser getUser() {

        if (null == SecurityContextHolder.getContext().getAuthentication()) {
            return new LoginUser(1L); //默认 Admin
        }

        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
            throw new AuthException(AuthExceptionEnum.NOT_LOGIN_ERROR);
        } else {
            return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }

    @Override
    public String getToken() {
        return TokenUtil.getToken();
    }


    @Override
    public String getZbxToken() {
        return null;
    }

    @Override
    public boolean hasLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        } else {
            if (authentication instanceof AnonymousAuthenticationToken) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public Long getUserId() {
        return getUser().getId();
    }

    @Override
    public boolean hasRole(String roleName) {
        return getUser().getRoleTips().contains(roleName);
    }

    @Override
    public boolean hasAnyRoles(String roleNames) {
        boolean hasAnyRole = false;
        if (this.hasLogin() && roleNames != null && roleNames.length() > 0) {
            for (String role : roleNames.split(",")) {
                if (hasRole(role.trim())) {
                    hasAnyRole = true;
                    break;
                }
            }
        }
        return hasAnyRole;
    }

    @Override
    public boolean hasPermission(String permission) {
        return getUser().getPermissions().contains(permission);
    }

    @Override
    public boolean isAdmin() {
//        List<Long> roleList = getUser().getRoleList();
//        if(roleList != null){
//            for (Long integer : roleList) {
//                String singleRoleTip = ConstantFactory.me().getSingleRoleTip(integer);
//                if (singleRoleTip.equals(Const.ADMIN_NAME)) {
//                    return true;
//                }
//            }
//        }
        return false;
    }

    @Override
    public boolean oauth2Flag() {
        String account = getUser().getAccount();
        if (account.startsWith(ConstantsContext.getOAuth2UserPrefix())) {
            return true;
        } else {
            return false;
        }
    }
}
