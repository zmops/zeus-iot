package com.zmops.iot.web.sys.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.exception.enums.AuthExceptionEnum;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.core.util.RsaUtil;
import com.zmops.iot.core.util.SaltUtil;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.sys.dto.UserDto;
import com.zmops.iot.web.sys.dto.param.UserParam;
import com.zmops.iot.web.sys.factory.UserFactory;
import com.zmops.zeus.driver.service.ZbxUser;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 用户管理
 **/
@Service
public class SysUserService {

    @Autowired
    private ZbxUser zbxUser;

    @Autowired
    private SysUserGroupService sysUserGroupService;

    /**
     * 用户列表
     *
     * @param userParam
     * @return
     */
    public Pager<UserDto> userList(UserParam userParam) {
        QSysUser qSysUser = new QSysUser();
        StringBuilder sql = new StringBuilder("select u.account, u.name, u.email, u.phone, u.role_id,r.name role_name," +
                "u.user_group_id,g.group_name user_group_name, u.status, u.create_user, u.update_user, u.create_time, u.update_time, u.user_id FROM sys_user u");
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
        int           count     = qSysUser.findCount();
        return new Pager<>(pagedList, count);
    }

    /**
     * 添加用戶
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUser createUser(UserDto user) {
        // 判断账号是否重复
        checkByAccount(user.getAccount());

        // 完善账号信息
        String password   = user.getPassword();
        String decryptPwd = "";
        try {
            password = RsaUtil.decryptPwd(password);
            decryptPwd = password;
        } catch (Exception e) {
            throw new ServiceException(BizExceptionEnum.PWD_DECRYPT_ERR);
        }

        String salt = SaltUtil.getRandomSalt();
        password = SaltUtil.md5Encrypt(password, salt);

        SysUser newUser = UserFactory.createUser(user, password, salt);
        //取对应的ZBX用户组ID
        Long       usrZbxId = sysUserGroupService.getZabUsrGrpId(user.getUserGroupId());
        JSONObject result   = JSONObject.parseObject(zbxUser.userAdd(user.getAccount(), decryptPwd, usrZbxId));
        JSONArray  userids  = result.getJSONArray("userids");
        newUser.setZbxId(String.valueOf(userids.get(0)));
        DB.save(newUser);
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
        SysUser sysUser = UserFactory.editUser(user, oldUser);
        //取对应的ZBX用户组ID
        Long usrZbxId = sysUserGroupService.getZabUsrGrpId(user.getUserGroupId());
        zbxUser.userUpdate(oldUser.getZbxId(), usrZbxId);

        DB.save(sysUser);
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
        zbxUser.userDelete(zbxIds);

        new QSysUser().userId.in(user.getUserIds()).delete();
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
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     */
    public void changePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = LoginContextHolder.getContext().getUser();

        if (null == loginUser) {
            throw new ServiceException(AuthExceptionEnum.NOT_LOGIN_ERROR);
        }
        SysUser user = new QSysUser().userId.eq(loginUser.getId()).findOne();

        try {
            oldPassword = RsaUtil.decryptPwd(oldPassword);
            newPassword = RsaUtil.decryptPwd(newPassword);
        } catch (Exception e) {
            throw new ServiceException(BizExceptionEnum.PWD_DECRYPT_ERR);
        }

        oldPassword = SaltUtil.md5Encrypt(oldPassword, user.getSalt());

        if (user.getPassword().equals(oldPassword)) {
            newPassword = SaltUtil.md5Encrypt(newPassword, user.getSalt());
            user.setPassword(newPassword);
            DB.update(user);
        } else {
            throw new ServiceException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
        }
    }
}
