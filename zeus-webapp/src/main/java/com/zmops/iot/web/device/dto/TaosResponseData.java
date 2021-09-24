package com.zmops.iot.web.device.dto;

import lombok.Data;

/**
 * @author nantian created at 2021/8/2 16:59
 */

@Data
public class TaosResponseData {

    private String status;

    private String[] head;

    private String[][] column_meta;

    private String[][] data;


}
