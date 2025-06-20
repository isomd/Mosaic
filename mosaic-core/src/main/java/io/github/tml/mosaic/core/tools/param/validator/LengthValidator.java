package io.github.tml.mosaic.core.tools.param.validator;

import io.github.tml.mosaic.core.tools.param.ConfigItem;
import io.github.tml.mosaic.core.tools.param.validator.core.ConfigValidator;
import io.github.tml.mosaic.core.tools.param.validator.core.ValidationResult;

import java.util.Collection;
import java.util.Map;

/**
 * 长度校验器（支持字符串、集合、数组等）
 */
public class LengthValidator implements ConfigValidator {
    
    private static final String MIN_LENGTH_KEY = "minLength";
    private static final String MAX_LENGTH_KEY = "maxLength";
    private static final String EXACT_LENGTH_KEY = "length";
    
    @Override
    public ValidationResult validate(Object value, ConfigItem configItem) {
        if (value == null) {
            return ValidationResult.success();
        }
        
        Map<String, Object> validation = configItem.getValidation();
        if (validation == null) {
            return ValidationResult.success();
        }
        
        int actualLength = getLength(value);
        ValidationResult result = ValidationResult.success();
        
        // 精确长度检查
        if (validation.containsKey(EXACT_LENGTH_KEY)) {
            int expectedLength = getIntValue(validation, EXACT_LENGTH_KEY, -1);
            if (expectedLength >= 0 && actualLength != expectedLength) {
                result.addError(String.format("Length must be exactly %d, but was %d", 
                    expectedLength, actualLength));
            }
        } else {
            // 最小长度检查
            if (validation.containsKey(MIN_LENGTH_KEY)) {
                int minLength = getIntValue(validation, MIN_LENGTH_KEY, 0);
                if (actualLength < minLength) {
                    result.addError(String.format("Length must be at least %d, but was %d", 
                        minLength, actualLength));
                }
            }
            
            // 最大长度检查
            if (validation.containsKey(MAX_LENGTH_KEY)) {
                int maxLength = getIntValue(validation, MAX_LENGTH_KEY, Integer.MAX_VALUE);
                if (actualLength > maxLength) {
                    result.addError(String.format("Length must be at most %d, but was %d", 
                        maxLength, actualLength));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public boolean supports(String validationType) {
        return "length".equals(validationType) || 
               MIN_LENGTH_KEY.equals(validationType) || 
               MAX_LENGTH_KEY.equals(validationType);
    }
    
    @Override
    public int getOrder() {
        return 400;
    }
    
    private int getLength(Object value) {
        if (value instanceof String) {
            return ((String) value).length();
        } else if (value instanceof Collection) {
            return ((Collection<?>) value).size();
        } else if (value.getClass().isArray()) {
            if (value instanceof Object[]) {
                return ((Object[]) value).length;
            } else if (value instanceof int[]) {
                return ((int[]) value).length;
            } else if (value instanceof long[]) {
                return ((long[]) value).length;
            } else if (value instanceof double[]) {
                return ((double[]) value).length;
            } else if (value instanceof boolean[]) {
                return ((boolean[]) value).length;
            }
            // 其他基本类型数组可以继续扩展
        }
        
        // 对于其他类型，使用toString()的长度
        return value.toString().length();
    }
    
    private int getIntValue(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}