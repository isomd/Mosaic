package io.github.tml.mosaic.install.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Cube配置项信息
 */
@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class CubeConfigItem {
    
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
            
            // 基本类型转换
            if (targetType == String.class) {
                return (T) defaultValue.toString();
            } else if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(defaultValue.toString());
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return (T) Boolean.valueOf(defaultValue.toString());
            } else if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(defaultValue.toString());
            } else if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(defaultValue.toString());
            }
            
            return (T) defaultValue;
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
        // 必填项检查
        if (required && (value == null || (value instanceof String && ((String) value).trim().isEmpty()))) {
            return false;
        }
        
        // 类型检查
        if (value != null && !isValidType(value)) {
            return false;
        }
        
        // 自定义验证规则检查
        return validateCustomRules(value);
    }

    private boolean isValidType(Object value) {
        if (type == null) {
            return true;
        }

        switch (type.toLowerCase()) {
            case "string":
                return value instanceof String;
            case "integer":
                return value instanceof Integer;
            case "boolean":
                return value instanceof Boolean;
            case "long":
                return value instanceof Long;
            case "double":
                return value instanceof Double;
            default:
                return false;
        }
    }
    
    private boolean validateCustomRules(Object value) {
        if (validation == null || validation.isEmpty()) {
            return true;
        }
        
        // 可以根据需要扩展更多验证规则
        return true;
    }
    
    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isBooleanString(String str) {
        return "true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str);
    }
    
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}