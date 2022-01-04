package com.zmops.iot.web.device.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ServiceExecuteDto {

    @NotBlank
    private String deviceId;
    @NotNull
    private Long   serviceId;

    private List<ServiceParam> serviceParams;

    @Data
    public static class ServiceParam {
        @NotBlank
        private String key;
        @NotBlank
        private String value;
    }
}
