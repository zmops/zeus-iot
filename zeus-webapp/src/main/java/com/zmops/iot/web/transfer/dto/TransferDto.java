package com.zmops.iot.web.transfer.dto;

import lombok.Data;

/**
 * @author yefei
 **/
@Data
public class TransferDto {

    private String name;

    private String createtime;

    private String logpath;

    private String readDataCount;

    private String lagSize;


    private String parseSuccess;
    private String parseError;

    private String parseTotal;

    private String sendSuccess;
    private String sendError;
    private String sendTotal;

    private String readspeedKb;

    private String readspeed;

    private String sendspeed;

    private String runningStatus;

    private String readLastError;

    private String parseLastError;

    private String sendLastError;

}
