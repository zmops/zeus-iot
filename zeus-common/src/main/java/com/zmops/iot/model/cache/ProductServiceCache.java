package com.zmops.iot.model.cache;


import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 **/
public class ProductServiceCache extends AbstractCache<Long, String> {

    public ProductServiceCache() {
        super();
    }

    public void updateProductService(Map<Long, String> map) {
        update(map);
    }

    public String getServiceName(Long id) {
        return Optional.ofNullable(get(id)).orElse(null);
    }

}
