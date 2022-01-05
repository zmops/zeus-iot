package com.zmops.iot.web.protocol.dto.param;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.protocol.ProtocolGatewayMqtt;
import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ProtocolGatewayParam extends BaseQueryParam {
    @NotNull(groups = BaseEntity.Update.class)
    private Long   protocolGatewayId;
    @NotBlank(groups = {BaseEntity.Create.class})
    private String name;
    private String protocolType;
    @NotNull(groups = BaseEntity.Create.class)
    private Long   protocolServiceId;
    private Long   protocolComponentId;
    private List<ProtocolGatewayMqtt> protocolGatewayMqttList;
    private String remark;
    private Long   tenantId;
    private Integer qos;

    @NotNull(groups = BaseEntity.Delete.class)
    private List<Long> protocolGatewayIds;
}
