package com.zmops.iot.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zmops.iot.constant.Constants;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectUtils {

    public static List<Map<String, Object>> digitUnits = new ArrayList<>();

    /**
     * 获取指定实体对象中字段的值
     *
     * @param o         实体对象
     * @param c         实体类
     * @param fieldName 字段名称
     * @return
     */
    public static Object getFieldValue(Object o, Class<?> c, String fieldName) {
        // 获取类中的全部定义字段
        Field[] fields = c.getDeclaredFields();
        // 循环遍历字段，获取字段相应的属性值
        for (Field field : fields) {
            // 设置字段可见，就可以用get方法获取属性值。
            field.setAccessible(true);
            try {
                if (fieldName.equals(field.getName())) {
                    return field.get(o);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static Object getFieldValue(Object o, String fieldName) {
        return getFieldValue(o, o.getClass(), fieldName);
    }

    public static void setFieldValue(Object o, Class<?> c, String fieldName, Object value) {
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals(fieldName)) {
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(fieldName, c);
                    Method wM = pd.getWriteMethod();
                    wM.invoke(o, value);
                } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static void setFieldValue(Object o, String fieldName, Object value) {
        setFieldValue(o, o.getClass(), fieldName, value);
    }

    /**
     * 实体对象转成Map      
     *
     * @param obj 实体对象    
     * @return      
     */
    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 检查IP地址是否合法
     *
     * @param ipAddress
     * @return
     */
    public static boolean isIpv4(String ipAddress) {

        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();

    }

    /**
     * 检查秘钥是否合法
     *
     * @param psk
     * @return
     */
    public static boolean validatePSK(String psk) {
        String pskpatern = "^([0-9a-f]{2})+$";
        Pattern pattern = Pattern.compile(pskpatern);
        Matcher matcher = pattern.matcher(psk);
        return matcher.matches();
    }

    /**
     * 检查字符串是不是数字
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 根据数字生成字母
     *
     * @param
     * @return
     */
    public static String num2letter(int number) {
        int start = 65;
        int base = 26;
        String str = "";
        int level = 0;
        do {
            if (level++ > 0) {
                number--;
            }
            int remainder = number % base;
            number = (number - remainder) / base;
            str = (char) (start + remainder) + str;
        } while (0 != number);
        return str;
    }

    /**
     * 返回两个list的去重并集
     *
     * @param output
     * @param extend
     * @return
     */
    public static <R> List<R> outputExtend(List<R> output, List<R> extend) {

        if (output.size() == 1 && output.get(0).equals("extend")) {
            return output;
        }
        Set<R> result = new HashSet<>();
        for (R o : output) {
            result.add(o);
        }
        for (R e : extend) {
            result.add(e);
        }
        return new ArrayList<>(result);
    }

    /**
     * 返回两个list的交集
     *
     * @param array1
     * @param array2
     * @return
     */
    public static List<?> arrayIntersectKey(List<?> array1, List<?> array2) {
        array1.retainAll(array2);
        return array1;
    }

    /**
     * Convert timestamp to string representation. Return 'Never' if 0.
     *
     * @return
     */
    public static String date2str(String format, String seconds) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    public static String convertUnits(String value, String units) {
        int convert = Constants.ITEM_CONVERT_WITH_UNITS;
        @SuppressWarnings("unused")
        boolean byteStep = false, pow = false, ignoreMillisec = false, length = false;
        if ("unixtime".equals(units)) {
            return date2str(Constants.DATE_TIME_FORMAT_SECONDS, value);
        }
        // special processing of uptime
        if ("uptime".equals(units)) {
            return convertUnitsUptime(value);
        }

        if ("s".equals(units)) {
            return convertUnitsS(value, false);
        }
        // black list of units that should have no multiplier prefix (K, M, G
        // etc) applied
        List<String> blackList = Lists.newArrayList("%", "ms", "rpm", "RPM");

        // add to the blacklist if unit is prefixed with '!'
        if (units != null && units.startsWith("!")) {
            units = units.substring(1);
            blackList.add(units);
        }
        if (blackList.contains(units) || !StringUtils.hasText(units)) {
            if (!CommonRegexpResolver.resolve("\\.\\d+$", "0.01").isEmpty()) {
                BigDecimal decimal = new BigDecimal(value);
                value = decimal
                        .setScale(decimal.abs().compareTo(new BigDecimal(0.01)) == 1 ? 2 : 6, RoundingMode.HALF_UP)
                        .toString();
            }
            value = value.replaceAll("^([\\-0-9]+)(\\.)([0-9]*?)[0]+$", "$1$2$3");
            if (value.endsWith(".")) {
                value = value.substring(0, value.length() - 1);
            }
            return value + " " + units;
        }
        int step;
        // if one or more items is B or Bps, then Y-scale use base 8 and
        // calculated in bytes
        if (byteStep) {
            step = 1024;
        } else {
            switch (units) {
                case "Bps":
                case "B":
                    step = 1024;
                    convert = convert != 0 ? convert : 1;
                    break;
                case "b":
                case "bps":
                    convert = convert != 0 ? convert : 1;
                default:
                    step = 1000;
                    break;
            }
        }

        BigDecimal abs = new BigDecimal(value).abs();
        if (abs.compareTo(new BigDecimal(1)) == -1) {
            value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
            if (length && abs.compareTo(new BigDecimal(0)) != 0) {

            }
            return (value + " " + units).trim();
        }

        List<String> steps = Arrays.asList("", "K", "M", "G", "T", "P", "E", "Z", "Y");
        BigDecimal values = new BigDecimal(value);
        int i = 0;
        while (values.divide(new BigDecimal(step)).longValue() > 1 && i < steps.size()) {
            values = values.divide(new BigDecimal(step));
            i++;
        }
        if (values.intValue() == step) {
            values = values.divide(new BigDecimal(step));
            i++;
        }
        value = values.setScale(10, RoundingMode.HALF_UP).toString().replaceAll("^([\\-0-9]+)(\\.)([0-9]*?)[0]+$",
                "$1$2$3");
        if (value.endsWith(".")) {
            value = value.substring(0, value.length() - 1);
        }
        if (value.contains(".")) {
            int idx = value.lastIndexOf('.') + 3;
            if (idx < value.length()) {
                value = value.substring(0, idx);
            }
        }
        return value + " " + steps.get(i) + units;

    }

    // @SuppressWarnings("unused")
    // String step = "";
    // int convert = Constants.ITEM_CONVERT_WITH_UNITS;
    // // special processing for unix timestamps
    // if ("unixtime".equals(units)) {
    // return date2str(Constants.DATE_TIME_FORMAT_SECONDS, value);
    // }
    // // special processing of uptime
    // if ("uptime".equals(units)) {
    // return convertUnitsUptime(value);
    // }
    //
    // if ("s".equals(units)) {
    // return convertUnitsS(value, false);
    // }
    // // black list of units that should have no multiplier prefix (K, M, G
    // // etc) applied
    // List<String> blackList = Arrays.asList("% ", "ms ", "rpm ", "RPM ");
    // // add to the blacklist if unit is prefixed with '!'
    // if (!StringUtils.isBlank(units) && units.startsWith("!")) {
    // blackList.add(units.substring(0, 1));
    // }
    // // any other unit
    // if (blackList.contains(units) || StringUtils.isBlank(units)) {
    // if (CommonRegexpResolver.resolve("\\.\\d*$", value).size() > 0) {
    // String format = Math.abs(Double.parseDouble(
    // value)) >= Constants.ARGUS_UNITS_ROUNDOFF_THRESHOLD ? "%."
    // + Constants.ARGUS_UNITS_ROUNDOFF_UPPER_LIMIT
    // + "f" : "%."
    // + Constants.ARGUS_UNITS_ROUNDOFF_LOWER_LIMIT
    // + "f";
    // value = formt(format, value);
    // }
    // value = value.replaceAll("^([\\-0-9]+)(\\.)([0-9]*)[0]+$", "$1$2$3");
    // value = value.split("\\.")[0];
    // return value.trim();
    // }
    // switch (units) {
    // case "Bps":
    // case "B":
    // step = Constants.ARGUS_KIBIBYTE;
    // break;
    // case "b":
    // case "bps":
    // default:
    // step = "1000";
    // }
    // double abs = 0.0;
    // if (Double.parseDouble(value) < 0) {
    // abs = Math.abs(Double.parseDouble(value));
    // } else {
    // abs = Double.parseDouble(value);
    // }
    // if (abs < 1) {
    // value = String.valueOf(round(value,
    // Constants.ARGUS_UNITS_ROUNDOFF_MIDDLE_LIMIT));
    // return value + " " + units;
    // }
    //
    // if (isEmpty(digitUnits)) {
    // initDigitUnits();
    // for (Map<String, Object> map : digitUnits) {
    // map.put("value", String.valueOf((long) Math.pow(1024, (int)
    // map.get("pow"))));
    // }
    // }
    // Map<String, Object> valUnit = new HashMap<>();
    // valUnit.put("pow", 0);
    // valUnit.put("short", "");
    // valUnit.put("value", value);
    // if (Double.parseDouble(value) == 0) {
    // for (Map<String, Object> map : digitUnits) {
    // if (abs >= Double.parseDouble(value)) {
    // valUnit = map;
    // } else {
    // break;
    // }
    // }
    // }
    // if (round(value, Constants.ARGUS_UNITS_ROUNDOFF_MIDDLE_LIMIT).intValue()
    // > 0) {
    // double newVal = new BigDecimal(formt("%.10f", value))
    // .divide(new BigDecimal(formt("%.10f",
    // String.valueOf(valUnit.get("value")))),
    // Constants.ARGUS_PRECISION_10,
    // BigDecimal.ROUND_HALF_UP)
    // .doubleValue();
    // valUnit.put("value", newVal);
    // } else {
    // valUnit.put("value", 0);
    // }
    // String desc = "";
    // switch (convert) {
    // case 0:
    // units = units.trim();
    // case 1:
    // desc = String.valueOf(valUnit.get("short"));
    // break;
    // }
    // value = String.valueOf(round(String.valueOf(valUnit.get("value")),
    // Constants.ARGUS_UNITS_ROUNDOFF_UPPER_LIMIT))
    // .replaceAll("^([\\-0-9]+)(\\.)([0-9]*)[0]+$", "$1$2$3");
    //
    // value = value.split("\\.")[0];
    //
    // if (Double.parseDouble(value) == 0) {
    // value = "0";
    // }
    // return value + " " + desc + units;
    // }

    @SuppressWarnings("unused")
    private static void initDigitUnits() {
        Map<String, Object> obj = new HashMap<>();
        obj.put("pow", 0);
        obj.put("short", "");
        digitUnits.add(obj);
        obj = new HashMap<>();
        obj.put("pow", 1);
        obj.put("short", "K");
        digitUnits.add(obj);
        obj = new HashMap<>();
        obj.put("pow", 2);
        obj.put("short", "M");
        digitUnits.add(obj);
        obj = new HashMap<>();
        obj.put("pow", 3);
        obj.put("short", "G");
        digitUnits.add(obj);
        obj = new HashMap<>();
        obj.put("pow", 4);
        obj.put("short", "T");
        digitUnits.add(obj);
        obj = new HashMap<>();
        obj.put("pow", 5);
        obj.put("short", "P");
        digitUnits.add(obj);
        obj = new HashMap<>();
        obj.put("pow", 6);
        obj.put("short", "E");
        digitUnits.add(obj);
        obj = new HashMap<>();
        obj.put("pow", 7);
        obj.put("short", "Z");
        digitUnits.add(obj);
        obj = new HashMap<>();
        obj.put("pow", 8);
        obj.put("short", "Y");
        digitUnits.add(obj);
    }


    /**
     * 将时间段转换为可读格式。 使用以下单位：年、月、日、小时、分钟、秒和毫秒。 仅显示三个最高单位：y m d，m d h，d h mm等等。
     * 如果某个值等于零，则忽略该值。例如，如果周期为1y0m4d，它将显示为 1Y 4D，非1Y 0M 4D或1Y 4D H。
     *
     * @param value
     * @param ignore_millisec
     * @return
     */
    private static String convertUnitsS(String value, boolean ignore_millisec) {
        BigDecimal secs = round(new BigDecimal(value).multiply(BigDecimal.valueOf(1000)),
                Constants.ARGUS_UNITS_ROUNDOFF_UPPER_LIMIT).divide(BigDecimal.valueOf(1000));
        long sec = secs.longValue();
        String str = "";
        if (sec < 0) {
            sec = -sec;
            str = "-";
        }
        int[] steps = new int[]{
                Constants.SEC_PER_YEAR, Constants.SEC_PER_MONTH,
                Constants.SEC_PER_DAY, Constants.SEC_PER_HOUR,
                Constants.SEC_PER_MIN
        };
        List<String> unitsEn = new ArrayList<>(units.keySet());
        Map<String, Long> values = new LinkedHashMap<>();
        for (int s = 0; s < steps.length; s++) {
            int v = steps[s];
            if (sec < v) {
                continue;
            }
            long n = sec / v;
            sec %= v;
            values.put(unitsEn.get(s), n);
        }
        if (sec != 0) {
            values.put("s", sec);
        }
        if (!ignore_millisec) {
            values.put("ms", secs.subtract(BigDecimal.valueOf(secs.longValue())).multiply(BigDecimal.valueOf(1000)).longValue());
        }
        int size = 0;
        for (Entry<String, Long> ent : values.entrySet()) {
            str += " " + ent.getValue() + units.get(ent.getKey());
            if (++size == 3) {
                break;
            }
        }
        return !StringUtils.hasText(str) ? "0" : str.trim();
    }

    public static void main(String[] args) {
        System.out.println(convertUnitsS("86411.1", false));
    }

    private final static Map<String, String> units = ArgusMap.<String, String>linkedHashMapBuilder().put("y", "年").put("m", "月")
            .put("d", "天").put("h", "小时").put("mm", "分").put("s", "秒").put("ms", "毫秒").build();

    private static String convertUnitsUptime(String value) {
        int secs = round(value, 0).intValue();
        if (secs < 0) {
            value = "-";
            secs = -secs;
        } else {
            value = "";
        }
        int days = new BigDecimal(secs).divide(new BigDecimal(Constants.SEC_PER_DAY), RoundingMode.DOWN).intValue();
        secs -= days * Constants.SEC_PER_DAY;

        int hours = new BigDecimal(secs).divide(new BigDecimal(Constants.SEC_PER_HOUR), RoundingMode.DOWN).intValue();
        secs -= hours * Constants.SEC_PER_HOUR;

        int mins = new BigDecimal(secs).divide(new BigDecimal(Constants.SEC_PER_MIN), RoundingMode.DOWN).intValue();
        secs -= mins * Constants.SEC_PER_MIN;

        if (days != 0) {
            value += days + "天,";
        }
        value += LocalDateTimeUtils.formatTime(LocalDateTime.now().withHour(hours).withMinute(mins).withSecond(secs),
                "HH:mm:ss");
        return value;
    }

    /**
     * 计算差值 并保留digits位小数
     *
     * @param last
     * @param prev
     * @param digits
     * @return
     */
    public static double bcsub(String last, String prev, int digits) {
        double lastVal = Double.parseDouble(last);
        double prevVal = Double.parseDouble(prev);

        double res = lastVal - prevVal;
        if (digits == 0) {
            return res;
        } else {
            BigDecimal bg = new BigDecimal(res);
            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            return f1;
        }
    }

    /**
     * 格式化value 保留r位小数并四舍五入
     *
     * @param value
     * @param r
     * @return
     */
    public static BigDecimal round(String value, int r) {
        if (!StringUtils.hasText(value)) {
            return new BigDecimal("0");
        }
        BigDecimal bvalue = new BigDecimal(value);
        return round(bvalue, r);
    }

    public static BigDecimal round(BigDecimal value, int r) {
        return value.divide(new BigDecimal("1"), r, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 根据format 格式化value 小数点位数
     *
     * @param format
     * @param value
     * @return
     */
    public static String formt(String format, String value) {
        Formatter fmt = new Formatter();
        value = fmt.format(format, Double.parseDouble(value)).toString();
        fmt.close();
        return value;
    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    public static <K, V> Map<K, V> deepCopy(Map<K, V> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        Map<K, V> dest = (Map<K, V>) in.readObject();
        return dest;
    }

    /**
     * 根据map键值的交集 返回map1中的值
     *
     * @param map
     * @param map2
     * @return
     */
    public static <Key, V, VV> Map<Key, V> mapIntersectKey(Map<Key, V> map, Map<Key, VV> map2) {
        Set<Key> bigMapKey = map.keySet();
        Set<Key> smallMapKey = map2.keySet();
        Set<Key> differenceSet = Sets.intersection(bigMapKey, smallMapKey);
        Map<Key, V> result = Maps.newHashMap();
        for (Key key : differenceSet) {
            result.put(key, map.get(key));
        }
        return result;
    }

    /**
     * 根据map键值的差集 返回map1中的值
     *
     * @param map
     * @param map2
     * @return
     */
    public static <Key, V, VV> Map<Key, V> mapDiffKey(Map<Key, V> map, Map<Key, VV> map2) {
        Set<Key> bigMapKey = map.keySet();
        Set<Key> smallMapKey = map2.keySet();
        Set<Key> differenceSet = Sets.difference(bigMapKey, smallMapKey);
        Map<Key, V> result = Maps.newHashMap();
        for (Key key : differenceSet) {
            result.put(key, map.get(key));
        }
        return result;
    }

    public static String substr_replace(String source, String replacement, int start, Integer length) {
        if (length == null) {
            return new StringBuilder(source.substring(0, start)).append(replacement).toString();
        }
        // 如果start <0 表示从source结尾处n个开始替换
        if (start < 0) {
            start = source.length() + start;
        }
        if (length >= 0) {
            return new StringBuilder(source.substring(0, start)).append(replacement)
                    .append(source.substring(start + length)).toString();
        }
        return new StringBuilder(source.substring(0, start)).append(replacement)
                .append(source.substring(source.length() + length)).toString();

    }

    public static String substr_replace(String source, String replacement, int start) {
        return substr_replace(source, replacement, start, null);
    }

}
