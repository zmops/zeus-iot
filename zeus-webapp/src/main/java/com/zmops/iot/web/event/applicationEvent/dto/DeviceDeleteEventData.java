package com.zmops.iot.web.event.applicationEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDeleteEventData {
    private String deviceId;
    private String zbxId;
}
