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


import com.zmops.iot.domain.sys.SysLoginLog;
import com.zmops.iot.domain.sys.SysOperationLog;
import com.zmops.iot.web.constant.state.LogSucceed;
import com.zmops.iot.web.constant.state.LogType;

import java.time.LocalDateTime;

/**
 * 日志对象创建工厂
 *
 * @author fengshuonan
 */
public class LogFactory {

    /**
     * 创建操作日志
     */
    public static SysOperationLog createOperationLog(LogType logType, Long userId, String bussinessName,
                                                     String clazzName, String methodName, String msg, LogSucceed succeed,Long tenantId) {
        SysOperationLog operationLog = new SysOperationLog();
        operationLog.setLogType(logType.getMessage());
        operationLog.setLogName(bussinessName);
        operationLog.setUserId(userId);
        operationLog.setClassName(clazzName);
        operationLog.setMethod(methodName);
        operationLog.setCreateTime(LocalDateTime.now());
        operationLog.setSucceed(succeed.getMessage());
        operationLog.setMessage(msg);
        operationLog.setTenantId(tenantId);
        return operationLog;
    }

    /**
     * 创建登录日志
     */
    public static SysLoginLog createLoginLog(LogType logType, Long userId, String msg, String ip,Long tenantId) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setLogName(logType.getMessage());
        loginLog.setUserId(userId);
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setSucceed(LogSucceed.SUCCESS.getMessage());
        loginLog.setIpAddress(ip);
        loginLog.setMessage(msg);
        loginLog.setTenantId(tenantId);
        return loginLog;
    }
}
