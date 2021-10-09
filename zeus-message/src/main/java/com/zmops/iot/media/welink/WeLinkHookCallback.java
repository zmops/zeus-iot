/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.zmops.iot.media.welink;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.AlarmMessage;
import com.zmops.iot.media.AlarmCallback;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class WeLinkHookCallback implements AlarmCallback {

    @Autowired
    WeLinkSettingService weLinkSettingService;

    private static final int           HTTP_CONNECT_TIMEOUT            = 1000;
    private static final int           HTTP_CONNECTION_REQUEST_TIMEOUT = 1000;
    private static final int           HTTP_SOCKET_TIMEOUT             = 10000;
    private final        RequestConfig requestConfig;

    public WeLinkHookCallback() {
        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout(HTTP_CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(HTTP_CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(HTTP_SOCKET_TIMEOUT)
                .build();
    }

    /**
     * Send alarm message if the settings not empty
     */
    @Override
    public void doAlarm(List<AlarmMessage> alarmMessages) {
//        WeLinkSettings weLinkSettings = weLinkSettingService.get();
//        if (weLinkSettings == null || weLinkSettings.getWebhooks().isEmpty()) {
//            return;
//        }
//        weLinkSettings.getWebhooks().forEach(webHookUrl -> {
//            String accessToken = getAccessToken(webHookUrl);
//            alarmMessages.forEach(alarmMessage -> {
//                String content = String.format(
//                        Locale.US,
//                        weLinkSettings.getTextTemplate(),
//                        alarmMessage.getAlarmMessage()
//                );
//                sendAlarmMessage(webHookUrl, accessToken, content);
//            });
//        });
    }

    /**
     * Send alarm message to remote endpoint
     */
    private void sendAlarmMessage(WeLinkSettings.WebHookUrl webHookUrl, String accessToken, String content) {
        JSONObject appServiceInfo = new JSONObject();
        appServiceInfo.put("app_service_id", "1");
        appServiceInfo.put("app_service_name", webHookUrl.getRobotName());
        JSONArray groupIds = new JSONArray();
        Arrays.stream(webHookUrl.getGroupIds().split(",")).forEach(groupIds::add);
        JSONObject body = new JSONObject();
        body.put("app_service_info", appServiceInfo);
        body.put("app_msg_id", UUID.randomUUID().toString());
        body.put("group_id", groupIds);
        body.put("content", String.format(
                Locale.US, "<r><n></n><g>0</g><c>&lt;imbody&gt;&lt;imagelist/&gt;" +
                        "&lt;html&gt;&lt;![CDATA[&lt;DIV&gt;%s&lt;/DIV&gt;]]&gt;&lt;/html&gt;&lt;content&gt;&lt;![CDATA[%s]]&gt;&lt;/content&gt;&lt;/imbody&gt;</c></r>",
                content, content
        ));
        body.put("content_type", 0);
        body.put("client_app_id", "1");
        sendPostRequest(
                webHookUrl.getMessageUrl(), Collections.singletonMap("x-wlk-Authorization", accessToken), body.toString());
    }

    /**
     * Get access token from remote endpoint
     */
    private String getAccessToken(WeLinkSettings.WebHookUrl webHookUrl) {
        String accessTokenUrl = webHookUrl.getAccessTokenUrl();
        String clientId       = webHookUrl.getClientId();
        String clientSecret   = webHookUrl.getClientSecret();
        String response = sendPostRequest(
                accessTokenUrl, Collections.emptyMap(),
                String.format(Locale.US, "{\"client_id\":%s,\"client_secret\":%s}", clientId, clientSecret)
        );
        JSONObject responseJson = JSONObject.parseObject(response);
        return Optional.ofNullable(responseJson)
                .map(r -> r.getString("access_token"))
                .orElse("");
    }

    /**
     * Post rest invoke
     */
    private String sendPostRequest(String url, Map<String, String> headers, String requestBody) {
        String response = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setConfig(requestConfig);
            post.setHeader(HttpHeaders.ACCEPT, HttpHeaderValues.APPLICATION_JSON.toString());
            post.setHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString());
            headers.forEach(post::setHeader);
            StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                StatusLine statusLine = httpResponse.getStatusLine();
                if (statusLine != null) {
                    if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                        log.error("send to {} failure. Response code: {}, Response content: {}", url,
                                statusLine.getStatusCode(),
                                EntityUtils.toString(httpResponse.getEntity())
                        );
                    } else {
                        response = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return response;
    }


    @Override
    public String getType() {
        return "welink";
    }
}
