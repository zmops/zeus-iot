package com.zmops.zeus.driver.inteceptor;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.reflection.ForestMethod;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.parser.JsonParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by nantian on 2021-03-12 23:00
 *
 * @version 1.0  拦截器 基于 JSON 文件构建 JSON String 参数
 */
@Slf4j
public class TaosJsonBodyBuildInterceptor implements Interceptor<String> {

    private static final String NO_AUTH_TAG = "authTag";

    /**
     * 方法调用之前获取 JSON path
     */
    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Map<String, Object> paramMap = new HashMap<>();

        if (null != args && args.length > 0) {
            // 优先 Map 的处理
            if (args[0] instanceof Map) {
                paramMap.putAll((Map) args[0]);
            } else {
                Annotation[][] paramAnnos = method.getMethod().getParameterAnnotations();
                for (int i = 0; i < args.length; i++) {
                    if (paramAnnos[i].length > 0 && paramAnnos[i][0] instanceof ParamName) {
                        ParamName paramAnno = (ParamName) paramAnnos[i][0];
                        paramMap.put(paramAnno.value(), args[i]);
                    }
                }
            }
        }

        JsonPath jsonPath = method.getMethod().getAnnotation(JsonPath.class);
        if (null != jsonPath && StringUtils.isNotBlank(jsonPath.value())) {
            String body     = Objects.requireNonNull(JsonParseUtil.parse(jsonPath.value() + ".ftl", paramMap));
            String sendBody = StringEscapeUtils.unescapeJava(body);
            log.debug("\n" + sendBody + "\n");
            request.replaceBody(sendBody);
            request.setContentType("application/json");
        }
    }

    /**
     * Zabbix 接口异常返回异常信息捕捉
     */
    @Override
    public void onSuccess(String data, ForestRequest request, ForestResponse response) {
//        TaosResponseData responseData = JSON.parseObject(data, TaosResponseData.class);

//        if (null != responseData.getError()) {
//            log.error(response.getContent());
//            throw new ZbxApiException(responseData.getError().getCode(), responseData.getError().getData());
//        }
        response.setResult(data);
        Interceptor.super.onSuccess(data, request, response);
    }
}
