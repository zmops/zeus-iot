package com.zmops.iot.model.cache;


import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 **/
public class ProtocolServiceCache extends AbstractCache<Long, String> {

    public ProtocolServiceCache() {
        super();
    }

    public void updateName(Map<Long, String> map) {
        update(map);
    }

    public String getName(Long id) {
        return Optional.ofNullable(get(id)).orElse(null);
    }

}
