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
package com.zmops.iot.web.log.factory;


import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.sys.SysLoginLog;
import com.zmops.iot.domain.sys.SysOperationLog;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.constant.state.LogSucceed;
import com.zmops.iot.web.constant.state.LogType;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

/**
 * 日志操作任务创建工厂
 *
 * @author fengshuonan nantian
 */
@Slf4j
public class LogTaskFactory {

    public static TimerTask loginLog(final Long userId, final String ip) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    SysLoginLog loginLog = LogFactory.createLoginLog(LogType.LOGIN, userId, null, ip);
                    DB.insert(loginLog);
                } catch (Exception e) {
                    log.error("创建登录日志异常!", e);
                }
            }
        };
    }

    public static TimerTask loginLog(final String username, final String msg, final String ip) {
        return new TimerTask() {
            @Override
            public void run() {
                SysLoginLog loginLog = LogFactory.createLoginLog(
                        LogType.LOGIN_FAIL, null, "账号:" + username + "," + msg, ip);
                try {
                    DB.insert(loginLog);
                } catch (Exception e) {
                    log.error("创建登录失败异常!", e);
                }
            }
        };
    }

    public static TimerTask exitLog(final Long userId, final String ip) {
        return new TimerTask() {
            @Override
            public void run() {
                SysLoginLog loginLog = LogFactory.createLoginLog(LogType.EXIT, userId, null, ip);
                try {
                    DB.insert(loginLog);
                } catch (Exception e) {
                    log.error("创建退出日志异常!", e);
                }
            }
        };
    }

    public static TimerTask bussinessLog(final Long userId, final String bussinessName, final String clazzName, final String methodName, final String msg) {
        return new TimerTask() {
            @Override
            public void run() {
                SysOperationLog operationLog = LogFactory.createOperationLog(
                        LogType.BUSSINESS, userId, bussinessName, clazzName, methodName, msg, LogSucceed.SUCCESS);
                try {
                    DB.insert(operationLog);
                } catch (Exception e) {
                    log.error("创建业务日志异常!", e);
                }
            }
        };
    }

    public static TimerTask exceptionLog(final Long userId, final Throwable exception) {
        return new TimerTask() {
            @Override
            public void run() {
                String msg = ToolUtil.getExceptionMsg(exception);
                SysOperationLog operationLog = LogFactory.createOperationLog(
                        LogType.EXCEPTION, userId, "", null, null, msg, LogSucceed.FAIL);
                try {
                    DB.insert(operationLog);
                } catch (Exception e) {
                    log.error("创建异常日志异常!", e);
                }
            }
        };
    }
}
