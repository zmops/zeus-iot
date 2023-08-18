package com.zmops.iot.model.cache;


import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 **/
public class DeviceCache extends AbstractCache<String, String> {

    public DeviceCache() {
        super();
    }

    public void updateDeviceName(Map<String, String> map) {
        update(map);
    }

    public String getDeviceName(String id) {
        return Optional.ofNullable(get(id)).orElse(null);
    }

}
