package com.zmops.iot.domain.device;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class ApiParam {

    @NotNull
    private List<Params> params;
    @NotBlank
    private String deviceId;

    @Data
    public static class Params{

        @NotBlank
        private String deviceAttrKey;
        @NotBlank
        private String deviceAttrValue;
    }
}
