package com.zmops.iot.web.transfer.dto.param;

import com.zmops.iot.web.sys.dto.param.BaseQueryParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yefei
 **/
@Data
public class TransferParam extends BaseQueryParam {

    @NotNull
    private List<String> names;

    private String run;
}
