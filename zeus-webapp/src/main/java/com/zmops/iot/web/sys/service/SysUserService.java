package com.zmops.iot.web.sys.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.exception.enums.AuthExceptionEnum;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.core.util.SaltUtil;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.domain.sys.query.QSysRole;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.sys.dto.UserDto;
import com.zmops.iot.web.sys.dto.param.UserParam;
import com.zmops.iot.web.sys.factory.UserFactory;
import com.zmops.zeus.driver.entity.ZbxUserInfo;
import com.zmops.zeus.driver.service.ZbxUser;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zmops.iot.web.init.DeviceSatusScriptInit.GLOBAL_ADMIN_ROLE_CODE;

/**
 * @author yefei
 * <p>
 * 用户管理
 **/
@Service
@Order(Integer.MAX_VALUE)
public class SysUserService implements CommandLineRunner {

    @Autowired
    private ZbxUser zbxUser;

    @Autowired
    private SysUserGroupService sysUserGroupService;


    public Map<String, Object> getUserIndexInfo() {

        //获取当前用户角色列表
        LoginUser  user     = LoginContextHolder.getContext().getUser();
        List<Long> roleList = user.getRoleList();

        //用户没有角色无法显示首页信息
        if (roleList == null || roleList.size() == 0) {
            return null;
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("name", user.getName());
        return result;
    }

    /**
     * 用户列表
     *
     * @param userParam
     * @return
     */
    public Pager<UserDto> userList(UserParam userParam) {
        QSysUser qSysUser = new QSysUser();
        StringBuilder sql = new StringBuilder(
                "select u.account, u.name, u.email, u.phone, u.role_id,r.name role_name," +
                        "u.user_group_id,g.group_name user_group_name, u.status, u.create_user, " +
                        "u.update_user, u.create_time, u.update_time, u.user_id FROM sys_user u");

        sql.append(" LEFT JOIN sys_role r ON r.role_id = u.role_id ");
        sql.append(" LEFT JOIN sys_user_group g ON g.user_group_id = u.user_group_id ");

        if (ToolUtil.isNotEmpty(userParam.getName())) {
            sql.append(" where u.name like ?");
            qSysUser.name.contains(userParam.getName());
        }
        sql.append(" order by u.create_time desc ");

        DtoQuery<UserDto> dto = DB.findDto(UserDto.class, sql.toString());

        if (ToolUtil.isNotEmpty(userParam.getName())) {
            dto.setParameter("%" + userParam.getName() + "%");
        }
        List<UserDto> pagedList = dto.setFirstRow((userParam.getPage() - 1) * userParam.getMaxRow()).setMaxRows(userParam.getMaxRow()).findList();

        int count = qSysUser.findCount();

        return new Pager<>(pagedList, count);
    }

    /**
     * 添加用戶
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUser createUser(UserDto user) {
        // 判断账号是否重复
        checkByAccount(user.getAccount());
        //判断角色是否存在
        checkByRole(user.getRoleId());

        // 完善账号信息
        String password   = user.getPassword();
        String decryptPwd = "";
        try {
            password = new String(Hex.decodeHex(password));
            decryptPwd = password;
        } catch (Exception e) {
            throw new ServiceException(BizExceptionEnum.PWD_DECRYPT_ERR);
        }

        String salt = SaltUtil.getRandomSalt();
        password = SaltUtil.md5Encrypt(password, salt);

        SysUser newUser = UserFactory.createUser(user, password, salt);

        //取对应的ZBX用户组ID
        String usrZbxId = sysUserGroupService.getZabUsrGrpId(user.getUserGroupId());
        JSONObject result = JSONObject.parseObject(
                zbxUser.userAdd(user.getAccount(), decryptPwd, usrZbxId, ConstantsContext.getConstntsMap().get(GLOBAL_ADMIN_ROLE_CODE).toString()));

        JSONArray userids = result.getJSONArray("userids");
        newUser.setZbxId(String.valueOf(userids.get(0)));

        DB.save(newUser);
        updateUserCache();

        return newUser;
    }

    /**
     * 修改用户
     *
     * @param user
     * @return SysUser
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUser updateUser(UserDto user) {
        SysUser oldUser = new QSysUser().userId.eq(user.getUserId()).findOne();
        if (null == oldUser) {
            throw new ServiceException(BizExceptionEnum.USER_NOT_EXIST);
        }
        //判断角色是否存在
        checkByRole(user.getRoleId());

        SysUser sysUser = UserFactory.editUser(user, oldUser);
        //取对应的ZBX用户组ID
        String usrZbxId = sysUserGroupService.getZabUsrGrpId(user.getUserGroupId());
        zbxUser.userUpdate(oldUser.getZbxId(), usrZbxId);

        DB.save(sysUser);
        updateUserCache();
        return sysUser;
    }

    /**
     * 删除用户
     *
     * @param user
     * @return
     */
    public void deleteUser(UserParam user) {
        List<SysUser> list = new QSysUser().userId.in(user.getUserIds()).findList();
        if (ToolUtil.isEmpty(list)) {
            throw new ServiceException(BizExceptionEnum.USER_NOT_EXIST);
        }
        List<String> zbxIds = list.parallelStream().map(SysUser::getZbxId).collect(Collectors.toList());

        //删除 zbx 用户数据
        if (ToolUtil.isNotEmpty(zbxIds)) {
            List<ZbxUserInfo> zbxUserList = JSONObject.parseArray(zbxUser.getUser(zbxIds.toString()), ZbxUserInfo.class);
            if (ToolUtil.isNotEmpty(zbxUserList)) {
                zbxUser.userDelete(zbxUserList.parallelStream().map(ZbxUserInfo::getUserid).collect(Collectors.toList()));
            }
        }
        new QSysUser().userId.in(user.getUserIds()).delete();
        updateUserCache();
    }

    /**
     * 根据account、userId 检查用户是否已注册
     *
     * @param account
     */
    private void checkByAccount(String account) {
        int count = new QSysUser().account.equalTo(account).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.USER_ALREADY_REG);
        }
    }

    /**
     * 检查所选角色是否存在
     *
     * @param roleId 角色ID
     */
    private void checkByRole(Long roleId) {
        int count = new QSysRole().roleId.eq(roleId).findCount();
        if (count <= 0) {
            throw new ServiceException(BizExceptionEnum.ROLE_NOT_EXIST);
        }
    }

    /**
     * 修改密码
     *
     * @param oldPassword String
     * @param newPassword String
     */
    public void changePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = LoginContextHolder.getContext().getUser();

        if (null == loginUser) {
            throw new ServiceException(AuthExceptionEnum.NOT_LOGIN_ERROR);
        }
        SysUser user         = new QSysUser().userId.eq(loginUser.getId()).findOne();
        String  rawNewPasswd = "";
        try {
            oldPassword = new String(Hex.decodeHex(oldPassword));
            newPassword = new String(Hex.decodeHex(newPassword));
            rawNewPasswd = newPassword;
        } catch (Exception e) {
            throw new ServiceException(BizExceptionEnum.PWD_DECRYPT_ERR);
        }

        oldPassword = SaltUtil.md5Encrypt(oldPassword, user.getSalt());

        if (user.getPassword().equals(oldPassword)) {
            newPassword = SaltUtil.md5Encrypt(newPassword, user.getSalt());
            user.setPassword(newPassword);
            DB.update(user);

            zbxUser.updatePwd(user.getZbxId(), rawNewPasswd);
        } else {
            throw new ServiceException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
        }
    }

    public void updateUserCache() {
        DefinitionsUtil.updateSysUser(new QSysUser().findList());
    }

    @Override
    public void run(String... args) throws Exception {
        updateUserCache();
    }


    public void reset(Long userId) {
        SysUser user = new QSysUser().userId.eq(userId).findOne();
        user.setSalt(SaltUtil.getRandomSalt());
        user.setPassword(SaltUtil.md5Encrypt(ConstantsContext.getDefaultPassword(), user.getSalt()));
        DB.update(user);

        zbxUser.updatePwd(user.getZbxId(), ConstantsContext.getDefaultPassword());
    }
}
