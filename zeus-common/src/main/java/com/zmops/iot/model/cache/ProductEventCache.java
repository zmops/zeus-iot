package com.zmops.iot.model.cache;


import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 **/
public class ProductEventCache extends AbstractCache<Long, String> {

    public ProductEventCache() {
        super();
    }

    public void updateTriggerName(Map<Long, String> map) {
        update(map);
    }

    public String getTriggerName(Long id) {
        return Optional.ofNullable(get(id)).orElse(null);
    }

}
