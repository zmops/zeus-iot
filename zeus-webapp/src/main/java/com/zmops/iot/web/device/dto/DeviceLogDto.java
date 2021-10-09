package com.zmops.iot.web.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLogDto {
    private String logType;
    private LocalDateTime triggerTime;
    private String content;
    private String param;
    private String status;
}
