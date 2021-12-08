package com.zmops.iot.model.cache;

import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 **/
public class TenantNameCache extends AbstractCache<Long, String>{
    public TenantNameCache() {
        super();
    }

    public void updateTenantName(Map<Long, String> map) {
        update(map);
    }

    public String getTenantName(Long id) {
        return Optional.ofNullable(get(id)).orElse(null);
    }
}
