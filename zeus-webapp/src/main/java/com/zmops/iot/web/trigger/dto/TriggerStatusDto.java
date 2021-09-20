package com.zmops.iot.web.trigger.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author nantian created at 2021/9/20 15:37
 */

@Getter
@Setter
public class TriggerStatusDto {

    @NotBlank
    private String triggerId;

    @Pattern(regexp = "^[01]$")
    private String status;

    @NotBlank
    private String eventRuleId;
}
