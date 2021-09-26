

package com.zmops.iot.media.dingtalk;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class DingtalkSettings {

    private String           textTemplate;
    @Builder.Default
    private List<WebHookUrl> webhooks = new ArrayList<>();

    @AllArgsConstructor
    @ToString
    @Data
    public static class WebHookUrl {
        private final String secret;
        private final String url;
    }
}
