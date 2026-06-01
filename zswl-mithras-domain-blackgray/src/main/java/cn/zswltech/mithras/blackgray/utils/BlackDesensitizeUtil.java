package cn.zswltech.mithras.blackgray.utils;

import cn.zswltech.mithras.blackgray.annotation.DesensitizeField;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 字符串脱敏
 */
public class BlackDesensitizeUtil {

    @SneakyThrows
    public static <T> T desensitize(T data)  {
        if (Objects.isNull(data)) {
            return null;
        }
        Map<String, Field> fieldMap = new HashMap<>();
        collectField(data.getClass(), fieldMap);

        for (String fieldName : fieldMap.keySet()) {
            Field field = fieldMap.get(fieldName);
            ReflectionUtils.makeAccessible(field);
            Object value = field.get(data);
            if (!(value instanceof String) || !field.isAnnotationPresent(DesensitizeField.class)) {
                continue;
            }
            DesensitizeField annotation = field.getAnnotation(DesensitizeField.class);
            int prefix = annotation.ignorePrefix();
            int suffix = annotation.ignoreSuffix();
            String stringValue = (String) value;
            if (stringValue.length() > prefix + suffix) {
                field.set(data, String.join("", stringValue.substring(0, prefix), "****", stringValue.substring(stringValue.length() - suffix)));
            } else if (stringValue.length() > prefix && prefix > 0) {
                field.set(data, String.join("", stringValue.substring(0, prefix), "****"));
            } else if (stringValue.length() > suffix && suffix > 0) {
                field.set(data, String.join("", "****", stringValue.substring(stringValue.length() - suffix)));
            } else {
                field.set(data, "****");
            }
        }
        return data;
    }

    private static void collectField(Class<?> tableClass, Map<String, Field> fieldMap) {
        if (fieldMap == null) {
            fieldMap = new HashMap<>();
        }
        if (tableClass.equals(Object.class)) {
            return;
        }
        // 先找父类的字段，如有同名，子类覆盖父类
        Class<?> superClass = tableClass.getSuperclass();
        if (superClass != null
                && !superClass.equals(Object.class)
                && !Map.class.isAssignableFrom(superClass)
                && !Collection.class.isAssignableFrom(superClass)) {
            collectField(tableClass.getSuperclass(), fieldMap);
        }
        // 找本类的字段
        Field[] fields = tableClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            //排除静态字段
            if (!Modifier.isStatic(field.getModifiers())) {
                fieldMap.put(field.getName(), field);
            }
        }
    }
}
