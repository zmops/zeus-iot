package com.zmops.iot.web.sys.controller;

import com.google.common.base.Joiner;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import io.ebean.DB;
import io.ebean.SqlRow;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nantian created at 2021/8/1 21:56
 * <p>
 * 系统菜单
 */

@RequestMapping("/sys/menu")
@RestController
public class SysMenuController {

    /**
     * 获取 菜单列表及按钮
     *
     * @return
     */
    @RequestMapping("/list")
    public ResponseData getMenuList() {
        LoginUser  user     = LoginContextHolder.getContext().getUser();
        List<Long> roleList = user.getRoleList();
        if (ToolUtil.isEmpty(roleList)) {
            throw new ServiceException(BizExceptionEnum.USER_NOT_BIND_ROLE);
        }
        String sql = "select " +
                "       m1.menu_id AS id," +
                "       ( CASE WHEN ( m2.menu_id = 0 OR m2.menu_id IS NULL ) THEN 0 ELSE m2.menu_id END ) AS pId," +
                "       m1.NAME AS NAME," +
                "       m1.url," +
                "       m1.icon," +
                "       m1.menu_flag " +
                " FROM " +
                "       sys_menu m1" +
                "       LEFT JOIN sys_menu m2 ON m1.pcode = m2.CODE " +
                " WHERE" +
                "       m1.status = 'ENABLE' " +
                " AND m1.menu_id in (select menu_id from sys_role_menu where role_id in (:roleIds))" +
                " ORDER BY" +
                "       m1.menu_id ASC";

        List<SqlRow>      menuList = DB.sqlQuery(sql).setParameter("roleIds",roleList).findList();
        Map<String, List> map      = new HashMap<>(2);
        map.put("menu", menuList.parallelStream().filter(x -> "Y".equals(x.getString("menu_flag"))).collect(Collectors.toList()));
        map.put("button", menuList.parallelStream().filter(x -> "N".equals(x.getString("menu_flag"))).map(x -> x.getString("url")).collect(Collectors.toList()));
        return ResponseData.success(map);
    }
}
