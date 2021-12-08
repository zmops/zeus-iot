package com.zmops.iot.web.transfer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zmops.iot.model.cache.filter.CachedValueFilter;
import lombok.Data;

/**
 * @author yefei
 **/
@Data
@JsonSerialize(using = CachedValueFilter.class)
public class StatusDto {

    private String name;

    private String logpath;

    private String readDataSize;

    private String readDataCount;

    private String elaspedtime;

    private Lag lag;

    private ReaderStats readerStats;

    private ParserStats parserStats;

    private String readspeedKb;

    private String readspeed;

    private String runningStatus;

    private String senderStats;

    @Data
    public static class Lag {
        private String total;
        private String size;
        private String ftlags;
        private String sizeunit;

    }

    @Data
    public static class ReaderStats {
        private String errors;
        private String success;
        private String speed;
        private String trend;
        private String last_error;
    }

    @Data
    public static class ParserStats {
        private String errors;
        private String success;
        private String speed;
        private String trend;
        private String last_error;
    }

    @Data
    public static class SenderStatsObj {
        private String errors;
        private String success;
        private String speed;
        private String trend;
        private String last_error;
    }

}
