package com.zmops.iot.web.product.dto.param;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ProductAttrParam extends BaseQueryParam {

    @NotNull(groups = BaseEntity.Get.class, message = "请选择一个设备再查询")
    private String prodId;

    private String attrName;

    private String key;


    @NotEmpty(groups = BaseEntity.Delete.class)
    private List<Long> ids;
}
