package io.github.tml.mosaic.core.tools.param.validator;

import io.github.tml.mosaic.core.tools.param.ConfigItem;
import io.github.tml.mosaic.core.tools.param.validator.core.ConfigValidator;
import io.github.tml.mosaic.core.tools.param.validator.core.ValidationResult;


import java.util.Collection;
import java.util.Map;

/**
 * 非空校验器
 */
public class NotNullValidator implements ConfigValidator {
    
    private static final String ALLOW_EMPTY_KEY = "allowEmpty";
    private static final String TRIM_KEY = "trim";
    
    @Override
    public ValidationResult validate(Object value, ConfigItem configItem) {
        Map<String, Object> validation = configItem.getValidation();
        if (validation == null) {
            return ValidationResult.success();
        }
        
        boolean allowEmpty = getBooleanValue(validation, ALLOW_EMPTY_KEY, true);
        boolean shouldTrim = getBooleanValue(validation, TRIM_KEY, true);
        
        if (value == null) {
            return ValidationResult.failure("Value cannot be null");
        }
        
        if (!allowEmpty && isEmpty(value, shouldTrim)) {
            return ValidationResult.failure("Value cannot be empty");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public boolean supports(String validationType) {
        return "notNull".equals(validationType) || "required".equals(validationType);
    }
    
    @Override
    public int getOrder() {
        return 50; // 高优先级，先执行非空检查
    }
    
    private boolean isEmpty(Object value, boolean shouldTrim) {
        if (value == null) {
            return true;
        }
        
        if (value instanceof String) {
            String str = (String) value;
            return shouldTrim ? str.trim().isEmpty() : str.isEmpty();
        }
        
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        
        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }
        
        if (value.getClass().isArray()) {
            if (value instanceof Object[]) {
                return ((Object[]) value).length == 0;
            }
            // 其他基本类型数组的长度检查
            return java.lang.reflect.Array.getLength(value) == 0;
        }
        
        return false;
    }
    
    private boolean getBooleanValue(Map<String, Object> map, String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }
}