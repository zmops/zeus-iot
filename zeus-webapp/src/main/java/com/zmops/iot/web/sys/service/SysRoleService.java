package com.zmops.iot.web.sys.service;

import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.exception.enums.AuthExceptionEnum;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.core.tree.DefaultTreeBuildFactory;
import com.zmops.iot.domain.sys.SysRole;
import com.zmops.iot.domain.sys.SysRoleMenu;
import com.zmops.iot.domain.sys.query.QSysMenu;
import com.zmops.iot.domain.sys.query.QSysRole;
import com.zmops.iot.domain.sys.query.QSysRoleMenu;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.node.TreeNode;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.sys.dto.SysRoleDto;
import com.zmops.iot.web.sys.dto.param.RoleParam;
import io.ebean.DB;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yefei
 **/
@Service
public class SysRoleService {

    /**
     * 角色列表
     *
     * @return
     */
    public List<SysRole> list(String name) {
        QSysRole qSysRole = new QSysRole();
        if (ToolUtil.isNotEmpty(name)) {
            qSysRole.name.contains(name);
        }
        return qSysRole.orderBy("create_time desc").findList();
    }

    /**
     * 角色新增
     *
     * @param sysRoleDto
     * @return
     */
    public SysRole create(SysRoleDto sysRoleDto) {
        checkSysRoleExist(sysRoleDto.getName(), -1L);
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleDto, sysRole);
        DB.save(sysRole);
        return sysRole;
    }

    /**
     * 角色修改
     *
     * @param sysRoleDto
     * @return
     */
    public SysRole update(SysRoleDto sysRoleDto) {
        checkSysRoleExist(sysRoleDto.getName(), sysRoleDto.getRoleId());
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleDto, sysRole);
        DB.update(sysRole);
        return sysRole;
    }

    /**
     * 角色删除
     *
     * @param sysRoleParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(RoleParam sysRoleParam) {
        int count = new QSysUser().roleId.in(sysRoleParam.getRoleIds()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.ROLE_HAS_BIND_USER);
        }
        new QSysRoleMenu().roleId.in(sysRoleParam.getRoleIds()).delete();
        new QSysRole().roleId.in(sysRoleParam.getRoleIds()).delete();
    }

    /**
     * 角色绑定菜单
     *
     * @param roleParam
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindMenu(RoleParam roleParam) {
        checkRoleId(roleParam.getRoleId());
        checkMenuId(roleParam.getMenuIds());
        new QSysRoleMenu().roleId.eq(roleParam.getRoleId()).delete();
        List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();
        for (Long menuId : roleParam.getMenuIds()) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(roleParam.getRoleId());
            sysRoleMenuList.add(sysRoleMenu);
        }
        DB.saveAll(sysRoleMenuList);
    }

    private void checkRoleId(Long roleId) {
        LoginUser user = LoginContextHolder.getContext().getUser();
        if (null == user) {
            return;
        }
        if (user.getRoleList().contains(roleId)) {
            throw new ServiceException(BizExceptionEnum.CANNOT_MODIFY_OWNER_MENUS);
        }
    }

    /**
     * 检查授权的菜单 是否存在或是否有权授权此菜单
     */
    private void checkMenuId(List<Long> menuIds) {
        List<Long> paramMuenuIds = new ArrayList<>(menuIds);
        LoginUser  user          = LoginContextHolder.getContext().getUser();
        if (null == user) {
            throw new ServiceException(AuthExceptionEnum.NOT_LOGIN_ERROR);
        }
        List<Long> adminMenuIds = new QSysMenu().select(QSysMenu.alias().menuId).adminFlag.eq("Y").findSingleAttributeList();
        menuIds.removeAll(adminMenuIds);
        List<Long> lists = new QSysRoleMenu().select(QSysRoleMenu.Alias.menuId).menuId.in(menuIds).roleId.in(user.getRoleList()).findSingleAttributeList();
        paramMuenuIds.removeAll(lists);
        if (ToolUtil.isNotEmpty(paramMuenuIds)) {
            throw new ServiceException(BizExceptionEnum.MENU_NOT_EXIST_OR_NO_PRERMISSION);
        }
    }

    /**
     * 根据角色名称 、Id检查角色是否已存在
     *
     * @param name
     */
    private void checkSysRoleExist(String name, Long roleId) {
        int count;
        if (roleId >= 0) {
            count = new QSysRole().name.equalTo(name).roleId.ne(roleId).findCount();
        } else {
            count = new QSysRole().name.equalTo(name).findCount();
        }
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.ROLE_HAS_EXIST);
        }
    }

    public List<TreeNode> bindedMenu(Long roleId) {
        LoginUser user = LoginContextHolder.getContext().getUser();
        if (null == user) {
            return Collections.emptyList();
        }
        //取当前操作者 可以授权的菜单
        String sql = "select " +
                "       m1.menu_id AS id," +
                "       ( CASE WHEN ( m2.menu_id = 0 OR m2.menu_id IS NULL ) THEN 0 ELSE m2.menu_id END ) AS pId," +
                "       m1.NAME," +
                "       m1.url," +
                "       m1.menu_flag " +
                " FROM " +
                "       sys_menu m1" +
                "       LEFT JOIN sys_menu m2 ON m1.pcode = m2.CODE " +
                " WHERE" +
                "       m1.status = 'ENABLE' " +
                " AND " +
                "       m1.menu_id in (select menu_id from sys_role_menu where role_id in (:roleIds) and admin_flag ='N' )" +
                " ORDER BY" +
                "       m1.sort ASC";
        List<TreeNode> allMenuList = DB.findDto(TreeNode.class, sql).setParameter("roleIds", user.getRoleList()).findList();

        //取被授权用户 已授权的菜单
        List<Long> selectMenuIdList = new QSysRoleMenu().select(QSysRoleMenu.Alias.menuId).roleId.eq(roleId).findSingleAttributeList();
        for (TreeNode menuTreeNode : allMenuList) {
            if (selectMenuIdList.contains(menuTreeNode.getId())) {
                menuTreeNode.setIsChecked(true);
            }
        }
        DefaultTreeBuildFactory<TreeNode> treeBuildFactory = new DefaultTreeBuildFactory<>();
        treeBuildFactory.setRootParentId("0");
        return treeBuildFactory.doTreeBuild(allMenuList);
    }


}
