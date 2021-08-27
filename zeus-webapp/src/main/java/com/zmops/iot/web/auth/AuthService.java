package com.zmops.iot.web.auth;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.zmops.iot.core.auth.cache.SessionManager;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.exception.AuthException;
import com.zmops.iot.core.auth.exception.enums.AuthExceptionEnum;
import com.zmops.iot.core.auth.jwt.JwtTokenUtil;
import com.zmops.iot.core.auth.jwt.payload.JwtPayLoad;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.core.util.HttpContext;
import com.zmops.iot.core.util.RsaUtil;
import com.zmops.iot.core.util.SaltUtil;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.web.constant.state.ManagerStatus;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.log.LogManager;
import com.zmops.iot.web.log.factory.LogTaskFactory;
import com.zmops.iot.web.sys.dto.LoginUserDto;
import com.zmops.iot.web.sys.factory.UserFactory;
import com.zmops.zeus.driver.service.ZbxUser;
import io.ebean.DB;
import io.ebean.SqlRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zmops.iot.constant.ConstantsContext.getJwtSecretExpireSec;
import static com.zmops.iot.constant.ConstantsContext.getTokenHeaderName;
import static com.zmops.iot.core.util.HttpContext.getIp;

@Service
@Transactional(readOnly = true)
public class AuthService {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ZbxUser zbxUser;


    /**
     * 用户名 和  密码 登陆
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public LoginUserDto login(String username, String password) {

        SysUser user = new QSysUser().account.eq(username).findOne();

        // 账号不存在
        if (null == user) {
            throw new AuthException(AuthExceptionEnum.NOT_EXIST_ERROR);
        }

        // 账号被冻结
        if (!user.getStatus().equals(ManagerStatus.OK.getCode())) {
            throw new AuthException(AuthExceptionEnum.ACCOUNT_FREEZE_ERROR);
        }

        //验证账号密码是否正确
        try {
            password = RsaUtil.decryptPwd(password);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionEnum.USERNAME_PWD_ERROR);
        }

        String requestMd5 = SaltUtil.md5Encrypt(password, user.getSalt());
        String dbMd5      = user.getPassword();
        if (dbMd5 == null || !dbMd5.equalsIgnoreCase(requestMd5)) {
            throw new AuthException(AuthExceptionEnum.USERNAME_PWD_ERROR);
        }

        int i = DB.sqlUpdate("update sys_user set zbx_token = :token where account = :account")
                .setParameter("token", zbxUser.userLogin(username, password))
                .setParameter("account", username)
                .execute();

        if (i == 0) {
            throw new ServiceException(BizExceptionEnum.ZBX_TOKEN_SAVE_ERROR);
        }

        return LoginUserDto.buildLoginUser(user, login(user));
    }

    /**
     * 用户名 登陆
     *
     * @param user 用户
     * @return
     */
    public String login(SysUser user) {

        //记录登录日志
        LogManager.me().executeLog(LogTaskFactory.loginLog(user.getUserId(), getIp()));

        //TODO key的作用
        JwtPayLoad payLoad = new JwtPayLoad(user.getUserId(), user.getAccount(), "xxxx");

        //创建token
        String token = JwtTokenUtil.generateToken(payLoad);

        //创建登录会话
        sessionManager.createSession(token, user(user.getAccount()));

        //创建cookie
        addLoginCookie(token);

        return token;
    }


    /**
     * 写入 登陆 Cookie
     *
     * @param token
     */
    public void addLoginCookie(String token) {
        //创建cookie
        Cookie authorization = new Cookie(getTokenHeaderName(), token);
        authorization.setMaxAge(getJwtSecretExpireSec().intValue());
        authorization.setHttpOnly(true);
        authorization.setPath("/");
        HttpServletResponse response = HttpContext.getResponse();
        response.addCookie(authorization);
    }


    public void logout() {
        String token = LoginContextHolder.getContext().getToken();
        logout(token);
    }

    public void logout(String token) {

        //记录退出日志
        LogManager.me().executeLog(LogTaskFactory.exitLog(LoginContextHolder.getContext().getUser().getId(), getIp()));

        //删除Auth相关cookies
        Cookie[] cookies = HttpContext.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String tokenHeader = getTokenHeaderName();
                if (tokenHeader.equalsIgnoreCase(cookie.getName())) {
                    Cookie temp = new Cookie(cookie.getName(), "");
                    temp.setMaxAge(0);
                    temp.setPath("/");
                    HttpContext.getResponse().addCookie(temp);
                }
            }
        }

        //删除会话
        sessionManager.removeSession(token);
    }


    public LoginUser user(String account) {

        SysUser user = new QSysUser().account.eq(account).findOne();

        LoginUser loginUser = UserFactory.createLoginUser(user);

//        //用户角色数组
        Long[] roleArray = Convert.toLongArray(user.getRoleId());
//
//        //如果角色是空就直接返回
//        if (roleArray == null || roleArray.length == 0) {
//            return loginUser;
//        }

//        //获取用户角色列表
        List<Long> roleList = new ArrayList<>();
//        List<String> roleNameList = new ArrayList<>();
//        List<String> roleTipList  = new ArrayList<>();
        for (Long roleId : roleArray) {
            roleList.add(roleId);
//            roleNameList.add(ConstantFactory.me().getSingleRoleName(roleId));
//            roleTipList.add(ConstantFactory.me().getSingleRoleTip(roleId));
        }
        loginUser.setRoleList(roleList);
//        loginUser.setRoleNames(roleNameList);
//        loginUser.setRoleTips(roleTipList);
//
//        //根据角色获取系统的类型
//        List<String> systemTypes = this.menuMapper.getMenusTypesByRoleIds(roleList);
//
//        //通过字典编码
//        List<Map<String, Object>> dictsByCodes = dictService.getDictsByCodes(systemTypes);
//        loginUser.setSystemTypes(dictsByCodes);
//
//        //设置权限列表
//        Set<String> permissionSet = new HashSet<>();
//        for (Long roleId : roleList) {
//            List<String> permissions = this.findPermissionsByRoleId(roleId);
//            if (permissions != null) {
//                for (String permission : permissions) {
//                    if (ToolUtil.isNotEmpty(permission)) {
//                        permissionSet.add(permission);
//                    }
//                }
//            }
//        }
//        loginUser.setPermissions(permissionSet);
//
//        //如果开启了多租户功能，则设置当前登录用户的租户标识
//        if (ConstantsContext.getTenantOpen()) {
//            String tenantCode   = TenantCodeHolder.get();
//            String dataBaseName = DataBaseNameHolder.get();
//            if (ToolUtil.isNotEmpty(tenantCode) && ToolUtil.isNotEmpty(dataBaseName)) {
//                loginUser.setTenantCode(tenantCode);
//                loginUser.setTenantDataSourceName(dataBaseName);
//            }
//
//            //注意，这里remove不代表所有情况，在aop remove
//            TenantCodeHolder.remove();
//            DataBaseNameHolder.remove();
//        }

        return loginUser;
    }

    public List<String> findPermissionsByRoleId(Long roleId) {
//        return menuMapper.getResUrlsByRoleId(roleId);
        return Collections.emptyList();
    }

    public boolean check(String[] roleNames) {
        LoginUser user = LoginContextHolder.getContext().getUser();
        if (null == user) {
            return false;
        }
        ArrayList<String> objects = CollectionUtil.newArrayList(roleNames);
        String            join    = CollectionUtil.join(objects, ",");
        if (LoginContextHolder.getContext().hasAnyRoles(join)) {
            return true;
        }
        return false;
    }


    public boolean checkAll(String code) {
        HttpServletRequest request = HttpContext.getRequest();
        LoginUser          user    = LoginContextHolder.getContext().getUser();
        if (null == user) {
            return false;
        }
        String       sql  = "select * from sys_menu where code = :code and menu_id in (SELECT menu_id from sys_role_menu where role_id = :roleId)";
        List<SqlRow> list = DB.sqlQuery(sql).setParameter("code", code).setParameter("roleId", user.getRoleList().get(0)).findList();
        if (list.size() > 0) {
            return true;
        }
        return false;
    }

}
