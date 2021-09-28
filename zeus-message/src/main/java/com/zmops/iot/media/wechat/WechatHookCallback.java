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

package com.zmops.iot.media.wechat;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class WechatHookCallback implements AlarmCallback {
    private static final int           HTTP_CONNECT_TIMEOUT            = 1000;
    private static final int           HTTP_CONNECTION_REQUEST_TIMEOUT = 1000;
    private static final int           HTTP_SOCKET_TIMEOUT             = 10000;
    private              RequestConfig requestConfig;

    @Autowired
    WechatSettingService wechatSettingService;

    @Override
    public String getType() {
        return "wechat";
    }

    public WechatHookCallback() {
        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout(HTTP_CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(HTTP_CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(HTTP_SOCKET_TIMEOUT)
                .build();
    }

    @Override
    public void doAlarm(List<AlarmMessage> alarmMessages) {
        WechatSettings wechatSettings = wechatSettingService.get();
        if (wechatSettings == null || wechatSettings.getWebhooks().isEmpty()) {
            return;
        }
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {
            wechatSettings.getWebhooks().forEach(url -> {
                alarmMessages.forEach(alarmMessage -> {
                    String requestBody = String.format(
                            wechatSettings.getTextTemplate(), alarmMessage.getAlarmMessage()
                    );
                    sendAlarmMessage(httpClient, url, requestBody);
                });
            });
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void sendAlarmMessage(CloseableHttpClient httpClient, String url, String requestBody) {
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost post = new HttpPost(url);
            post.setConfig(requestConfig);
            post.setHeader(HttpHeaders.ACCEPT, HttpHeaderValues.APPLICATION_JSON.toString());
            post.setHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString());
            StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            httpResponse = httpClient.execute(post);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine != null && statusLine.getStatusCode() != HttpStatus.SC_OK) {
                log.error("send wechat alarm to {} failure. Response code: {} ", url, statusLine.getStatusCode());
            }
        } catch (Throwable e) {
            log.error("send wechat alarm to {} failure.", url, e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }

            }
        }
    }
}
