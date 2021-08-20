package com.zmops.iot.config.ebean;

import com.zmops.iot.core.auth.context.LoginContextHolder;
import io.ebean.config.CurrentUserProvider;
import org.springframework.stereotype.Component;

/**
 * @author nantian created at 2021/7/30 20:53
 * <p>
 * 根据当前登录用户 获取 userId，用于 userId 日志记录
 */

@Component
public class CurrentUser implements CurrentUserProvider {

    @Override
    public Object currentUser() {
        return LoginContextHolder.getContext().getUser().getId();
    }
}
