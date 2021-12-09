package com.zmops.iot.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用正则解析器
 * 
 * @author yefei
 */
public class CommonRegexpResolver {

    /**
     * @param regexp 正则表达式
     * @param input 待匹配的字符串
     * @param groupNames 分组名
     * @return
     */
    public static Map<String, Collection<String>> resolve(String regexp, String input, List<String> groupNames) {
        if (input == null) {
            return new HashMap<>();
        }
        Multimap<String, String> res = ArrayListMultimap.create();
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(input);
        if (Pattern.matches(regexp, input)) {
            while (matcher.find()) {
                groupNames.forEach(o -> {
                    if (matcher.group(o) != null) {
                        res.put(o, matcher.group(o));
                    }
                    for (int i = 0; i <= matcher.groupCount(); i++) {
                        res.put(String.valueOf(i), matcher.group(i));
                    }
                });
            }
        } else {
            int i = 0;
            while (matcher.find()) {
                res.put(String.valueOf(i++), matcher.group());
            }
        }
        return res.asMap();
    }

    public static Map<Integer, Collection<String>> resolve(String regexp, String input) {
        if (input == null) {
            return new HashMap<Integer, Collection<String>>();
        }
        Multimap<Integer, String> res = ArrayListMultimap.create();
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(input);
        if (Pattern.matches(regexp, input)) {
            while (matcher.find()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    res.put(i, matcher.group(i));
                }
            }
        } else {
            int i = 0;
            while (matcher.find()) {
                res.put(i++, matcher.group());
            }
        }
        return res.asMap();
    }

    
    // public static void main(String[] args) {
    // final String regexp =
    // "^(?<sign>[\\-+])?(?<number>(\\d)+)(?<suffix>[smhdw])?$";
    // Map<String, Collection<String>> matches =
    // CommonRegexpResolver.resolve(regexp, "-5m",
    // Arrays.asList("sign","number","suffix"));
    // matches.entrySet().forEach(o->{
    // System.out.println(o.getKey()+"->"+o.getValue());
    // });
    // }
    
    
}
