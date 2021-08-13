package com.zmops.iot.model.cache.filter;

import com.alibaba.fastjson.serializer.AfterFilter;
import com.zmops.iot.domain.BaseDto;
import com.zmops.iot.util.DefinitionsUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author yefei
 */
public class CachedValueFilter extends AfterFilter {

    public final static String SUFFIX = "Name";


    @Override
    public void writeAfter(Object object) {
        if (!(object instanceof BaseDto)) {
            return;
        }
        for (Field field : getAllFields(object.getClass())) {
            CachedValue[] cachedValues = field.getAnnotationsByType(CachedValue.class);
            if (cachedValues == null || cachedValues.length == 0) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value == null) {
                    continue;
                }
                boolean exists = false;
                for (CachedValue cachedValue : cachedValues) {
                    String res = getCachedValue(cachedValue, value);
                    if (res != null) {
                        exists = true;
                        writeKeyValue(field.getName() + Optional.of(cachedValue.suffix()).orElse(SUFFIX),
                                res);
                    } else {
                        exists = exists || cachedValue.type().isNullable();
                    }
                }
                // 只要有一个存在 就不置为 null
                if (!exists) {
                    writeKeyValue(field.getName(), null);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(getAllFields(clazz.getSuperclass()));
        }
        return fields;
    }


    private String getCachedValue(CachedValue cachedValue, Object value) {
        switch (cachedValue.type()) {
            case Dictionary:
                if (value instanceof String) {
                    return DefinitionsUtil.getNameByVal(cachedValue.value(), (String) value);
                }
                if (value instanceof Integer) {
                    return DefinitionsUtil.getNameByVal(cachedValue.value(), value.toString());
                }
                break;
            case SysUserName:
                if (value instanceof Long) {
                    return DefinitionsUtil.getSysUserName((long) value);
                }
                if (value instanceof Integer) {
                    return DefinitionsUtil.getSysUserName(((Integer) value).longValue());
                }
                break;
            default:
                return null;
        }
        return null;
    }
}
