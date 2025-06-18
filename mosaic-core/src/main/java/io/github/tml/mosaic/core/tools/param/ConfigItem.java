package io.github.tml.mosaic.core.tools.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.tml.mosaic.core.tools.param.validator.core.ConfigValidationManager;
import io.github.tml.mosaic.core.tools.param.validator.core.ValidationResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 配置项信息
 */
@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigItem {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("required")
    private boolean required = false;

    @JsonProperty("defaultValue")
    private Object defaultValue;

    @JsonProperty("validation")
    private Map<String, Object> validation;

    /**
     * 获取类型安全的默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue(Class<T> targetType) {
        if (defaultValue == null) {
            return null;
        }

        try {
            if (targetType.isInstance(defaultValue)) {
                return (T) defaultValue;
            }

            return convertValue(defaultValue, targetType);
        } catch (Exception e) {
            log.warn("Failed to convert default value for config item: {}, error: {}",
                    name, e.getMessage());
            return null;
        }
    }

    /**
     * 验证配置项值是否有效
     */
    public boolean validateValue(Object value) {
        return getValidationResult(value).isValid();
    }

    /**
     * 获取详细的校验结果
     */
    public ValidationResult getValidationResult(Object value) {
        return ConfigValidationManager.getInstance().validateConfigItem(value, this);
    }

    /**
     * 类型转换工具方法
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }

        String str = value.toString().trim();

        try {
            if (targetType == String.class) {
                return (T) str;
            } else if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(str);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return (T) Boolean.valueOf(str);
            } else if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(str);
            } else if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(str);
            } else if (targetType == Float.class || targetType == float.class) {
                return (T) Float.valueOf(str);
            }

            return (T) value;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Cannot convert value '%s' to type %s", value, targetType.getSimpleName()), e);
        }
    }
}