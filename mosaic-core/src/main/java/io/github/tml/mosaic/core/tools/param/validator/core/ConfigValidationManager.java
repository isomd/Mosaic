package io.github.tml.mosaic.core.tools.param.validator.core;

import io.github.tml.mosaic.core.tools.param.ConfigItem;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 配置校验管理器
 */
@Slf4j
public class ConfigValidationManager {
    
    private final ValidatorRegistry validatorRegistry;
    private final boolean failFast;

    // 校验管理器
    private static volatile ConfigValidationManager INSTANCE = new ConfigValidationManager();
    
    public ConfigValidationManager() {
        this(true);
    }

    /**
     * 获取校验管理器实例
     */
    public static ConfigValidationManager getInstance() {
        return INSTANCE;
    }
    
    public ConfigValidationManager(boolean failFast) {
        this.failFast = failFast;
        this.validatorRegistry = ValidatorRegistry.getInstance();
        
        // 初始化默认校验器
        this.validatorRegistry.initializeDefaultValidators();
    }
    
    /**
     * 执行完整的配置校验
     */
    public ValidationResult validateConfigItem(Object value, ConfigItem configItem) {
        ValidationResult result = ValidationResult.success()
            .withConfigItemName(configItem.getName());
        
        // 基础校验：必填项和类型检查
        ValidationResult basicResult = performBasicValidation(value, configItem);
        if (!basicResult.isValid()) {
            return basicResult.withConfigItemName(configItem.getName());
        }
        
        // 自定义校验规则
        Map<String, Object> validation = configItem.getValidation();
        if (validation != null && !validation.isEmpty()) {
            for (Map.Entry<String, Object> entry : validation.entrySet()) {
                String validationType = entry.getKey();
                
                ValidationResult customResult = performCustomValidation(
                    value, configItem, validationType);
                result.merge(customResult);
                
                // 快速失败模式
                if (!customResult.isValid() && failFast) {
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * 批量校验配置项
     */
    public ValidationResult validateConfigs(Map<String, Object> configs, 
                                          Map<String, ConfigItem> configItems) {
        ValidationResult overallResult = ValidationResult.success();
        
        for (Map.Entry<String, ConfigItem> entry : configItems.entrySet()) {
            String configName = entry.getKey();
            ConfigItem configItem = entry.getValue();
            Object value = configs.get(configName);
            
            ValidationResult itemResult = validateConfigItem(value, configItem);
            overallResult.merge(itemResult);
            
            if (!itemResult.isValid() && failFast) {
                break;
            }
        }
        
        return overallResult;
    }
    
    /**
     * 注册自定义校验器
     */
    public void registerValidator(ConfigValidator validator) {
        validatorRegistry.registerValidator(validator);
    }
    
    /**
     * 基础校验：必填项和类型检查
     */
    private ValidationResult performBasicValidation(Object value, ConfigItem configItem) {
        // 必填项检查
        if (configItem.isRequired() && isEmpty(value)) {
            return ValidationResult.failure("Required configuration is missing");
        }
        
        // 类型检查
        if (value != null && !isValidType(value, configItem.getType())) {
            return ValidationResult.failure(
                String.format("Expected type '%s' but got '%s'", 
                    configItem.getType(), 
                    value.getClass().getSimpleName()));
        }
        
        return ValidationResult.success();
    }
    
    /**
     * 自定义校验规则执行
     */
    private ValidationResult performCustomValidation(Object value, ConfigItem configItem, 
                                                   String validationType) {
        List<ConfigValidator> validators = validatorRegistry.getValidators(validationType);
        
        for (ConfigValidator validator : validators) {
            if (validator.supports(validationType)) {
                try {
                    ValidationResult result = validator.validate(value, configItem);
                    if (!result.isValid()) {
                        return result;
                    }
                } catch (Exception e) {
                    log.error("Validation error with validator: {}, config: {}, error: {}", 
                        validator.getName(), configItem.getName(), e.getMessage(), e);
                    return ValidationResult.failure("Validation failed: " + e.getMessage());
                }
            }
        }
        
        return ValidationResult.success();
    }
    
    private boolean isEmpty(Object value) {
        return value == null || 
               (value instanceof String && ((String) value).trim().isEmpty());
    }
    
    private boolean isValidType(Object value, String expectedType) {
        if (expectedType == null || expectedType.trim().isEmpty()) {
            return true;
        }
        
        switch (expectedType.toLowerCase()) {
            case "string":
                return value instanceof String;
            case "integer":
                return value instanceof Integer || isIntegerString(value);
            case "boolean":
                return value instanceof Boolean || isBooleanString(value);
            case "long":
                return value instanceof Long || isLongString(value);
            case "double":
                return value instanceof Double || isDoubleString(value);
            default:
                return true; // 未知类型默认通过
        }
    }
    
    private boolean isIntegerString(Object value) {
        if (!(value instanceof String)) return false;
        try {
            Integer.parseInt((String) value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isBooleanString(Object value) {
        if (!(value instanceof String)) return false;
        String str = ((String) value).toLowerCase();
        return "true".equals(str) || "false".equals(str);
    }
    
    private boolean isLongString(Object value) {
        if (!(value instanceof String)) return false;
        try {
            Long.parseLong((String) value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isDoubleString(Object value) {
        if (!(value instanceof String)) return false;
        try {
            Double.parseDouble((String) value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}