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
public class ProtocolServiceParam extends BaseQueryParam {
    @NotNull(groups = BaseEntity.Update.class)
    private Long   protocolServiceId;
    @NotBlank(groups = {BaseEntity.Create.class})
    private String name;
    private String effectProxy;
    private String protocolType;
    private String remark;
    private Long   tenantId;
    private String url;
    private String ip;
    private Integer port;
    private Integer msgLength;
    private String clientId;

    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> protocolServiceIds;
}
