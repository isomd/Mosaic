package io.github.tml.mosaic.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ClassUtils {

    // 定义常见类型集合（包括基本类型、包装类型和String）
    private static final Set<Class<?>> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(
            byte.class, short.class, int.class, long.class, float.class, double.class,
            char.class, boolean.class, void.class, // 基本类型 + void
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
            Character.class, Boolean.class, Void.class, // 包装类型
            String.class // 特殊引用类型
    ));

    /**
     * 检查传入的 Class 对象是否为基本类型、包装类型或 String
     * @param clazz 要检查的 Class 对象
     * @return 如果属于目标类型返回 true，否则 false
     */
    public static boolean isBasicType(Class<?> clazz) {
        return PRIMITIVE_TYPES.contains(clazz);
    }


    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Ignore
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    // 判断是否是JDK自带的类
    private static boolean isJdkClass(Class<?> clazz) {
        return clazz != null &&
                (clazz.getName().startsWith("java.") ||
                        clazz.getName().startsWith("javax."));
    }

    // 判断是否是Map类型（包括接口和实现类）
    private static boolean isMapType(Class<?> clazz) {
        if (clazz == null) return false;
        return Map.class.isAssignableFrom(clazz);
    }

    // 判断是否是自定义实体类
    public static boolean isCustomEntityClass(Class<?> clazz) {
        // 排除基本类型、数组、接口、枚举、注解、JDK类等
        return clazz != null &&
                !clazz.isArray() &&
                !clazz.isInterface() &&
                !clazz.isEnum() &&
                !clazz.isAnnotation() &&
                !isBasicType(clazz) &&
                !isJdkClass(clazz) &&
                !isMapType(clazz);
    }
}