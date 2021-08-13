
package com.zmops.iot.mediaType.welink;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class WeLinkSettings {

    private String           textTemplate;
    @Builder.Default
    private List<WebHookUrl> webhooks = new ArrayList<>();

    @AllArgsConstructor
    @Data
    @ToString
    public static class WebHookUrl {
        // The unique identity of the application, used for interface authentication to obtain access_token
        private final String clientId;
        // The application key is used for interface authentication to obtain access_token
        private final String clientSecret;
        // The url get access token
        private final String accessTokenUrl;
        // The url to send message
        private final String messageUrl;
        // Name display in group
        private final String robotName;
        // The groupIds message to send
        private final String groupIds;

        public static WebHookUrl generateFromMap(Map<String, String> params) {
            String clientId       = params.get("clientId");
            String clientSecret   = params.get("clientSecret");
            String accessTokenUrl = params.get("accessTokenUrl");
            String messageUrl     = params.get("messageUrl");
            String groupIds       = params.get("groupIds");
            String robotName      = params.getOrDefault("robotName", "robot");
            return new WebHookUrl(clientId, clientSecret, accessTokenUrl, messageUrl,
                    robotName, groupIds
            );
        }
    }
}
