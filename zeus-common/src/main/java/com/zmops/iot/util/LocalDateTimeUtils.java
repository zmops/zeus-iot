package com.zmops.iot.util;


import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;

public class LocalDateTimeUtils {

    // 一天的毫秒
    public static final long MILLISECONDS_PER_DAY = 24L * 3600 * 1000;
    // 获取当前时间的LocalDateTime对象
    // LocalDateTime.now();

    // 根据年月日构建LocalDateTime
    // LocalDateTime.of();

    // 比较日期先后
    // LocalDateTime.now().isBefore(),
    // LocalDateTime.now().isAfter(),

    // Date转换为LocalDateTime
    public static LocalDateTime convertDateToLDT(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    // LocalDateTime转换为Date
    public static Date convertLDTToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    // 获取指定日期的毫秒
    public static Long getMilliByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // 获取指定日期的秒
    public static Long getSecondsByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    // 获取指定日期的秒
    public static Long getSecondsByStr(String date) {
        return getSecondsByTime(dateToStamp(date));
    }

    /**
     * 根据秒获取时间
     *
     * @param seconds
     * @return
     */
    public static LocalDateTime getLDTBySeconds(Integer seconds) {
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.ofHours(8));
    }


    public static LocalDateTime getLDTByMilliSeconds(Long mills) {
        LocalDateTime time = getLDTBySeconds((int) (mills / 1000));
        return time.withNano((int) (mills % 1000));
    }

    // 获取指定时间的指定格式
    public static String formatTime(LocalDateTime time, String pattern) {
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatTime(LocalDateTime time) {
        return formatTime(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatTimeDate(LocalDateTime time) {
        return formatTime(time, "yyyy-MM-dd");
    }

    // 获取当前时间的指定格式
    public static String formatNow(String pattern) {
        return formatTime(LocalDateTime.now(), pattern);
    }

    // 日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
    public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
        return time.plus(number, field);
    }

    // 日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
    public static LocalDateTime minu(LocalDateTime time, long number, TemporalUnit field) {
        return time.minus(number, field);
    }

    /**
     * 获取两个日期的差 field参数为ChronoUnit.*
     *
     * @param startTime
     * @param endTime
     * @param field     单位(年月日时分秒)
     * @return
     */
    public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
        if (field == ChronoUnit.YEARS) {
            return period.getYears();
        }
        if (field == ChronoUnit.MONTHS) {
            return period.getYears() * 12 + period.getMonths();
        }
        return field.between(startTime, endTime);
    }

    // 获取一天的开始时间，2017,7,22 00:00
    public static LocalDateTime getDayStart(LocalDateTime time) {
        return time.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    // 获取一天的结束时间，2017,7,22 23:59:59.999999999
    public static LocalDateTime getDayEnd(LocalDateTime time) {
        return time.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    /*
     * 时间戳转 LocalDateTime
     */
    public static LocalDateTime convertDateToLocalDateTime(Long time) {
        if (time == null) {
            return null;
        }
        return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8));
    }

    /*
     * 时间戳转 String
     */
    public static String convertTimeToString(Long time, String pattern) {
        if (time == null) {
            return null;
        }
        return LocalDateTimeUtils.formatTime(LocalDateTimeUtils.getLDTByMilliSeconds(time), pattern);
    }

    public static LocalDateTime dateToStamp(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(str.replace("T", " "));
            return convertDateToLDT(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LocalDateTime.now();
    }

    public static String dateToStr(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(str.replace("T", " "));
            return formatTime(convertDateToLDT(date));
        } catch (Exception e) {
            return str;
        }
    }


    public static LocalDateTime getThisWeekMonday() {
        Calendar cal = Calendar.getInstance();
        //        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static void main(String[] args) {
        System.out.println(getSecondsByStr("2021-08-18T14:25:38.059"));
    }
}
