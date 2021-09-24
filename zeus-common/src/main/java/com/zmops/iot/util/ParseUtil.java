package com.zmops.iot.util;

import java.math.BigDecimal;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;

/**
 * @author yefei
 **/
public class ParseUtil {


    public static void main(String[] args) {
        System.out.println(getCommaFormat("2097180.19677"));
    }

    //每3位中间添加逗号的格式化显示
    public static String getCommaFormat(String value) {
        if (ToolUtil.isEmpty(value)) {
            return "0";
        }
        return getFormat(",###.##", new BigDecimal(value));
    }

    //自定义数字格式方法
    public static String getFormat(String style, BigDecimal value) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern(style);
        return df.format(value.doubleValue());
    }


    public static String formatLagSize(String size) {
        if (ToolUtil.isEmpty(size)) {
            return "0";
        }
        long bytes = Long.parseLong(size);
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
