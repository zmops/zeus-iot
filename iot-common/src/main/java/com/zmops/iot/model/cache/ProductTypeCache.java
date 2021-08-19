package com.zmops.iot.model.cache;


import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 **/
public class ProductTypeCache extends AbstractCache<Long, String> {

    public ProductTypeCache() {
        super();
    }

    public void updateProductType(Map<Long, String> map) {
        update(map);
    }

    public String getTypeName(Long id) {
        return Optional.ofNullable(get(id)).orElse(null);
    }

}
