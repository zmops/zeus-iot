package com.zmops.iot.domain.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yefei
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBody {
    private String msg;
    private String type;
    private List<Long> to;
    private Map<String, Object> body;
    private boolean persist = false;


    public void addBody(String k, Object v) {
        if (this.body == null) {
            this.body = new HashMap<>();
        }
        this.body.put(k, v);
    }
}
