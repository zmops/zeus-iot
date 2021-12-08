package com.zmops.iot.core.auth.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.util.FixLengthLinkedList;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.exception.enums.CoreExceptionEnum;
import com.zmops.iot.util.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yefei
 **/
@Slf4j
public class CommonInterceptor implements HandlerInterceptor {

//    FixLengthLinkedList<Integer> list = new FixLengthLinkedList<>(10);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

//        if (request instanceof CommonHttpServletRequestWrapper) {
//            CommonHttpServletRequestWrapper requestWrapper = (CommonHttpServletRequestWrapper) request;
//            int code = requestWrapper.getBody().hashCode();
//            log.debug("*************************" + code);
//            if (list.contains(code)) {
//                throw new ServiceException(CoreExceptionEnum.REPEATED_SUBMIT);
//            }
//            list.add(code);
//        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        pushTenantId2Body(request, handlerMethod);

        return true;
    }

    private void pushTenantId2Body(HttpServletRequest request, HandlerMethod handlerMethod) {
        try {

            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            if (ArrayUtils.isEmpty(methodParameters)) {
                return;
            }
            for (MethodParameter methodParameter : methodParameters) {
                Class clazz = methodParameter.getParameterType();

                if (request instanceof CommonHttpServletRequestWrapper) {
                    CommonHttpServletRequestWrapper requestWrapper = (CommonHttpServletRequestWrapper) request;
                    String body = requestWrapper.getBody();
                    JSONObject param = JSONObject.parseObject(body);
                    if (param == null || ToolUtil.isNotEmpty(param.getString("tenantId"))) {
                        return;
                    }
                    param.put("tenantId", LoginContextHolder.getContext().getUser().getTenantId());
                    requestWrapper.setBody(JSON.toJSONString(param));
                }

            }
        } catch (Exception e) {
            log.warn("fill userInfo to request body Error ", e);
        }
    }
}
