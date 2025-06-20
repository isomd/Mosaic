package io.github.tml.mosaic.core.tools.param.validator;

import io.github.tml.mosaic.core.tools.param.ConfigItem;
import io.github.tml.mosaic.core.tools.param.validator.core.ConfigValidator;
import io.github.tml.mosaic.core.tools.param.validator.core.ValidationResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 数值范围校验器
 */
@Slf4j
public class RangeValidator implements ConfigValidator {
    
    private static final String MIN_KEY = "min";
    private static final String MAX_KEY = "max";
    private static final String MIN_INCLUSIVE_KEY = "minInclusive";
    private static final String MAX_INCLUSIVE_KEY = "maxInclusive";
    
    @Override
    public ValidationResult validate(Object value, ConfigItem configItem) {
        if (value == null) {
            return ValidationResult.success();
        }
        
        Map<String, Object> validation = configItem.getValidation();
        if (validation == null) {
            return ValidationResult.success();
        }
        
        try {
            BigDecimal numValue = convertToNumber(value);
            if (numValue == null) {
                return ValidationResult.failure("Value must be numeric for range validation");
            }
            
            ValidationResult result = ValidationResult.success();
            
            // 检查最小值
            result.merge(validateMinimum(numValue, validation));
            
            // 检查最大值  
            result.merge(validateMaximum(numValue, validation));
            
            return result;
            
        } catch (Exception e) {
            log.warn("Range validation error for config item: {}, error: {}", 
                configItem.getName(), e.getMessage());
            return ValidationResult.failure("Range validation failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean supports(String validationType) {
        return "range".equals(validationType) || 
               MIN_KEY.equals(validationType) || 
               MAX_KEY.equals(validationType);
    }
    
    @Override
    public int getOrder() {
        return 200;
    }
    
    private ValidationResult validateMinimum(BigDecimal value, Map<String, Object> validation) {
        if (!validation.containsKey(MIN_KEY)) {
            return ValidationResult.success();
        }
        
        BigDecimal min = convertToNumber(validation.get(MIN_KEY));
        if (min == null) {
            return ValidationResult.failure("Invalid minimum value configuration");
        }
        
        boolean minInclusive = getBooleanValue(validation, MIN_INCLUSIVE_KEY, true);
        int comparison = value.compareTo(min);
        
        if ((minInclusive && comparison < 0) || (!minInclusive && comparison <= 0)) {
            return ValidationResult.failure(
                String.format("Value %s must be %s %s", 
                    value, minInclusive ? ">=" : ">", min));
        }
        
        return ValidationResult.success();
    }
    
    private ValidationResult validateMaximum(BigDecimal value, Map<String, Object> validation) {
        if (!validation.containsKey(MAX_KEY)) {
            return ValidationResult.success();
        }
        
        BigDecimal max = convertToNumber(validation.get(MAX_KEY));
        if (max == null) {
            return ValidationResult.failure("Invalid maximum value configuration");
        }
        
        boolean maxInclusive = getBooleanValue(validation, MAX_INCLUSIVE_KEY, true);
        int comparison = value.compareTo(max);
        
        if ((maxInclusive && comparison > 0) || (!maxInclusive && comparison >= 0)) {
            return ValidationResult.failure(
                String.format("Value %s must be %s %s", 
                    value, maxInclusive ? "<=" : "<", max));
        }
        
        return ValidationResult.success();
    }
    
    private BigDecimal convertToNumber(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
            return new BigDecimal(value.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
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