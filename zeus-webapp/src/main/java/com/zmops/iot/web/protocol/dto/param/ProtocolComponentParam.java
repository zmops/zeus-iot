package com.zmops.iot.web.protocol.dto.param;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ProtocolComponentParam extends BaseQueryParam {
    @NotNull(groups = BaseEntity.Update.class)
    private Long   protocolComponentId;
    @NotBlank(groups = {BaseEntity.Create.class})
    private String name;
    private String effectProxy;
    private String source;
    private String status;
    private String remark;
    private Long   tenantId;
    private String fileName;

    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> protocolComponentIds;
}
