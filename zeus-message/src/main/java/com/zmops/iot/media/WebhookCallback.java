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

package com.zmops.iot.media;

import com.zmops.iot.domain.alarm.AlarmMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;

import java.util.List;

@Slf4j
public class WebhookCallback implements AlarmCallback {
    private static final int HTTP_CONNECT_TIMEOUT = 1000;
    private static final int HTTP_CONNECTION_REQUEST_TIMEOUT = 1000;
    private static final int HTTP_SOCKET_TIMEOUT = 10000;

    private RequestConfig requestConfig;

    @Override
    public void doAlarm(List<AlarmMessage> alarmMessage,Long tenantId) {

    }

    @Override
    public String getType() {
        return "Dingding";
    }
//    private Gson              gson = new Gson();
//
//    public WebhookCallback(AlarmRulesWatcher alarmRulesWatcher) {
//        this.alarmRulesWatcher = alarmRulesWatcher;
//        requestConfig = RequestConfig.custom()
//                                     .setConnectTimeout(HTTP_CONNECT_TIMEOUT)
//                                     .setConnectionRequestTimeout(HTTP_CONNECTION_REQUEST_TIMEOUT)
//                                     .setSocketTimeout(HTTP_SOCKET_TIMEOUT)
//                                     .build();
//    }
//
//    @Override
//    public void doAlarm(List<AlarmMessage> alarmMessage) {
//        if (alarmRulesWatcher.getWebHooks().isEmpty()) {
//            return;
//        }
//
//        CloseableHttpClient httpClient = HttpClients.custom().build();
//        try {
//            alarmRulesWatcher.getWebHooks().forEach(url -> {
//                HttpPost post = new HttpPost(url);
//                post.setConfig(requestConfig);
//                post.setHeader(HttpHeaders.ACCEPT, HttpHeaderValues.APPLICATION_JSON.toString());
//                post.setHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString());
//
//                StringEntity          entity;
//                CloseableHttpResponse httpResponse = null;
//                try {
//                    entity = new StringEntity(gson.toJson(alarmMessage), StandardCharsets.UTF_8);
//                    post.setEntity(entity);
//                    httpResponse = httpClient.execute(post);
//                    StatusLine statusLine = httpResponse.getStatusLine();
//                    if (statusLine != null && statusLine.getStatusCode() != HttpStatus.SC_OK) {
//                        log.error("send alarm to " + url + " failure. Response code: " + statusLine.getStatusCode());
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    log.error("Alarm to JSON error, " + e.getMessage(), e);
//                } catch (IOException e) {
//                    log.error("send alarm to " + url + " failure.", e);
//                } finally {
//                    if (httpResponse != null) {
//                        try {
//                            httpResponse.close();
//                        } catch (IOException e) {
//                            log.error(e.getMessage(), e);
//                        }
//
//                    }
//                }
//            });
//        } finally {
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                log.error(e.getMessage(), e);
//            }
//        }
//    }
}
