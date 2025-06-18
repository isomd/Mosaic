package io.github.tml.mosaic.cube;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
public class CubeConfig {

    public CubeConfig(Map<String, Object> configValues) {
        this.configValues = configValues;
    }

    private Map<String, Object> configValues;

    /**
     * 获取配置值
     */
    public <T> T getConfig(String name, Class<T> type) {
        Object value = configValues.get(name);
        return convertValue(value, type);
    }

    /**
     * 获取配置值（带默认值）
     */
    public <T> T getConfig(String name, Class<T> type, T defaultValue) {
        T value = getConfig(name, type);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取所有配置（只读）
     */
    public Map<String, Object> getAllConfigs() {
        return Collections.unmodifiableMap(configValues);
    }

    /**
     * 类型转换
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> type) {
        if (value == null || type.isInstance(value)) {
            return (T) value;
        }
        try {
            String str = value.toString();
            if (type == String.class) return (T) str;
            if (type == Integer.class || type == int.class) return (T) Integer.valueOf(str);
            if (type == Boolean.class || type == boolean.class) return (T) Boolean.valueOf(str);
            if (type == Long.class || type == long.class) return (T) Long.valueOf(str);
            if (type == Double.class || type == double.class) return (T) Double.valueOf(str);

            return (T) value;
        } catch (Exception e) {
            return null;
        }
    }
}
