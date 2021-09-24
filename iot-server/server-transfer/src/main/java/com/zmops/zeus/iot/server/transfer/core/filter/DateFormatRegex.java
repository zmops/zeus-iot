package com.zmops.zeus.iot.server.transfer.core.filter;

import com.zmops.zeus.iot.server.transfer.api.Filter;
import com.zmops.zeus.iot.server.transfer.utils.TransferUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * date format with regex string for absolute file path.
 */
public class DateFormatRegex implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateFormatRegex.class);

    private static final String YEAR  = "YYYY";
    private static final String MONTH = "MM";
    private static final String DAY   = "DD";
    private static final String HOUR  = "HH";

    private static final String NORMAL_FORMATTER = "yyyyMMddHHmm";

    private static final String OFFSET_DAY  = "d";
    private static final String OFFSET_MIN  = "m";
    private static final String OFFSET_HOUR = "h";

    private int dayOffset    = 0;
    private int hourOffset   = 0;
    private int minuteOffset = 0;

    private String formattedTime = "";
    private String formattedRegex;
    private File   file;

    /**
     * set regex with current time
     *
     * @param regex
     * @return
     */
    public static DateFormatRegex ofRegex(String regex) {
        DateFormatRegex dateFormatRegex = new DateFormatRegex();
        dateFormatRegex.setRegexWithCurrentTime(regex);
        return dateFormatRegex;
    }

    @Override
    public boolean match() {
        // TODO: check with more regex
        return TransferUtils.regexMatch(file.getAbsolutePath(), formattedRegex);
    }

    public DateFormatRegex withOffset(String timeOffset) {
        String number = StringUtils.substring(timeOffset, 0, timeOffset.length() - 1);
        String mark   = StringUtils.substring(timeOffset, timeOffset.length() - 1);

        switch (mark) {
            case OFFSET_DAY:
                dayOffset = Integer.parseInt(number);
                break;
            case OFFSET_HOUR:
                hourOffset = Integer.parseInt(number);
                break;
            case OFFSET_MIN:
                minuteOffset = Integer.parseInt(number);
                break;
            default:
                LOGGER.warn("time offset is invalid, please check {}", timeOffset);
                break;
        }
        return this;
    }

    public DateFormatRegex withFile(File file) {
        this.file = file;
        return this;
    }

    /**
     * set regex with given time, replace the YYYYDDMM pattern with given time
     *
     * @param regex
     * @param time
     */
    public void setRegexWithTime(String regex, String time) {
        String[] regexList = StringUtils.splitByWholeSeparatorPreserveAllTokens(regex, File.separator, 0);

        List<String> formattedList = new ArrayList<>();
        for (String regexStr : regexList) {
            if (regexStr.contains(YEAR)) {
                String tmpRegexStr = regexStr.replace(YEAR, time.substring(0, 4))
                        .replace(MONTH, time.substring(4, 6))
                        .replace(DAY, time.substring(6, 8))
                        .replace(HOUR, time.substring(8, 10));
                formattedList.add(tmpRegexStr);
                formattedTime = time;
            } else {
                formattedList.add(regexStr);
            }
        }
        this.formattedRegex = StringUtils.join(formattedList, File.separatorChar);
        LOGGER.info("updated formatted regex is {}", this.formattedRegex);
    }

    public void setRegexWithCurrentTime(String regex) {
        String currentTime = TransferUtils.formatCurrentTimeWithOffset(NORMAL_FORMATTER, dayOffset, hourOffset, minuteOffset);
        setRegexWithTime(regex, currentTime);
    }

    public String getFormattedRegex() {
        return formattedRegex;
    }

    public String getFormattedTime() {
        return formattedTime;
    }
}
