package com.zmops.iot.model.cache.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zmops.iot.domain.BaseDto;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.LocalDateTimeUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author yefei
 */
public class CachedValueFilter extends JsonSerializer<Object> {

    public final static String SUFFIX = "Name";

    @Override
    public void serialize(Object object, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (!(object instanceof BaseDto)) {
            return;
        }

        jsonGenerator.writeStartObject();
        for (Field field : getAllFields(object.getClass())) {

            Object value = null;
            field.setAccessible(true);

            try {
                value = field.get(object);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value == null) {
                continue;
            }
            CachedValue[] cachedValues = field.getAnnotationsByType(CachedValue.class);
            if (cachedValues.length == 0) {
                if (value instanceof LocalDateTime) {
                    jsonGenerator.writeStringField(field.getName(), LocalDateTimeUtils.dateToStr(value.toString()));
                } else {
                    jsonGenerator.writeStringField(field.getName(), value.toString());
                }
                continue;
            }

            boolean exists = false;
            for (CachedValue cachedValue : cachedValues) {
                String res = getCachedValue(cachedValue, value);
                if (res != null) {
                    exists = true;
                    jsonGenerator.writeStringField(field.getName() + Optional.of(cachedValue.suffix()).orElse(SUFFIX), res);
                } else {
                    exists = exists || cachedValue.type().isNullable();
                }
            }
            // 只要有一个存在 就不置为 null
            if (!exists) {
                jsonGenerator.writeStringField(field.getName(), null);
            }
        }
        jsonGenerator.writeEndObject();
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
            case ProdType:
                if (value instanceof Long) {
                    return DefinitionsUtil.getTypeName((long) value);
                }
                break;
            default:
                return null;
        }
        return null;
    }


}
