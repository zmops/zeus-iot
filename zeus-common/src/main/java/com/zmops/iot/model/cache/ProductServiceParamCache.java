package com.zmops.iot.model.cache;


import com.zmops.iot.domain.product.ProductServiceParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 **/
public class ProductServiceParamCache extends AbstractCache<Long, List<ProductServiceParam>> {

    public ProductServiceParamCache() {
        super();
    }

    public void updateProductServiceParam(Map<Long, List<ProductServiceParam>> map) {
        update(map);
    }

    public List<ProductServiceParam> getServiceParam(Long id) {
        return Optional.ofNullable(get(id)).orElse(null);
    }

}
