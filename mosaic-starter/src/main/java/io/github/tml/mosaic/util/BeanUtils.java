package io.github.tml.mosaic.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {
    private static final Map<Class<?>, Field[]> FIELD_CACHE = new HashMap<>();
    public static void copyProperties(Object source, Object target){
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and destination objects cannot be null");
        }

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        // 获取源对象字段（缓存）
        Field[] sourceFields = getNonStaticFields(sourceClass);

        for (Field sourceField : sourceFields) {
            try {
                Field targetField = targetClass.getDeclaredField(sourceField.getName());

                // 类型一致才复制
                if (sourceField.getType().equals(targetField.getType())) {
                    sourceField.setAccessible(true);
                    targetField.setAccessible(true);

                    Object value = sourceField.get(source);
                    targetField.set(target, value);
                }
            } catch (NoSuchFieldException ignored) {
                // 目标没有该字段，跳过
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static Field[] getNonStaticFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, c -> {
            Field[] allFields = c.getDeclaredFields();
            int count = 0;
            for (Field f : allFields) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    count++;
                }
            }

            Field[] result = new Field[count];
            int index = 0;
            for (Field f : allFields) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    result[index++] = f;
                }
            }
            return result;
        });
    }
}
