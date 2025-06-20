package io.github.tml.mosaic.core.tools.param.validator;

import io.github.tml.mosaic.core.tools.param.ConfigItem;
import io.github.tml.mosaic.core.tools.param.validator.core.ConfigValidator;
import io.github.tml.mosaic.core.tools.param.validator.core.ValidationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 枚举值校验器
 */
@Slf4j
public class EnumValidator implements ConfigValidator {
    
    private static final String VALIDATION_TYPE = "enum";
    private static final String VALUES_KEY = "values";
    
    @Override
    public ValidationResult validate(Object value, ConfigItem configItem) {
        if (value == null) {
            return ValidationResult.success();
        }
        
        Map<String, Object> validation = configItem.getValidation();
        if (validation == null || !validation.containsKey(VALUES_KEY)) {
            return ValidationResult.failure("Enum validation requires 'values' configuration");
        }
        
        Object valuesObj = validation.get(VALUES_KEY);
        if (!(valuesObj instanceof List)) {
            return ValidationResult.failure("Enum 'values' must be a list");
        }
        
        @SuppressWarnings("unchecked")
        List<Object> allowedValues = (List<Object>) valuesObj;
        
        if (allowedValues.isEmpty()) {
            return ValidationResult.failure("Enum 'values' cannot be empty");
        }
        
        String valueStr = value.toString();
        boolean isValid = allowedValues.stream()
            .anyMatch(allowedValue -> {
                if (allowedValue == null) {
                    return false;
                }
                return allowedValue.toString().equals(valueStr);
            });
        
        if (!isValid) {
            return ValidationResult.failure(
                String.format("Value '%s' is not in allowed enum values: %s", 
                    valueStr, allowedValues));
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public boolean supports(String validationType) {
        return VALIDATION_TYPE.equals(validationType);
    }
    
    @Override
    public int getOrder() {
        return 100;
    }
}