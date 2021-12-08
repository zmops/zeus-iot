package com.zmops.iot.web.transfer.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class ConfigDto {

    private String name;
    private String batchSize;
    private String batchInterval;
    private String createtime;


}
