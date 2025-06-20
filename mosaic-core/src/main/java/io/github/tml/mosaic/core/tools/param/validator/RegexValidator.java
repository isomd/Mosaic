package io.github.tml.mosaic.core.tools.param.validator;

import io.github.tml.mosaic.core.tools.param.ConfigItem;
import io.github.tml.mosaic.core.tools.param.validator.core.ConfigValidator;
import io.github.tml.mosaic.core.tools.param.validator.core.ValidationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 正则表达式校验器
 */
@Slf4j
public class RegexValidator implements ConfigValidator {
    
    private static final String PATTERN_KEY = "pattern";
    private static final String MESSAGE_KEY = "message";
    private static final String FLAGS_KEY = "flags";
    
    // 缓存编译后的正则表达式，提高性能
    private final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();
    
    @Override
    public ValidationResult validate(Object value, ConfigItem configItem) {
        if (value == null) {
            return ValidationResult.success();
        }
        
        Map<String, Object> validation = configItem.getValidation();
        if (validation == null || !validation.containsKey(PATTERN_KEY)) {
            return ValidationResult.failure("Regex validation requires 'pattern' configuration");
        }
        
        String patternStr = validation.get(PATTERN_KEY).toString();
        if (patternStr == null || patternStr.trim().isEmpty()) {
            return ValidationResult.failure("Regex pattern cannot be empty");
        }
        
        String customMessage = validation.containsKey(MESSAGE_KEY) 
            ? validation.get(MESSAGE_KEY).toString() 
            : null;
        
        try {
            Pattern pattern = getCompiledPattern(patternStr, validation);
            String valueStr = value.toString();
            
            if (!pattern.matcher(valueStr).matches()) {
                String errorMessage = customMessage != null 
                    ? customMessage 
                    : String.format("Value '%s' does not match required pattern", valueStr);
                return ValidationResult.failure(errorMessage);
            }
            
            return ValidationResult.success();
            
        } catch (PatternSyntaxException e) {
            log.error("Invalid regex pattern for config item: {}, pattern: {}, error: {}", 
                configItem.getName(), patternStr, e.getMessage());
            return ValidationResult.failure("Invalid regex pattern: " + e.getDescription());
        } catch (Exception e) {
            log.warn("Regex validation error for config item: {}, error: {}", 
                configItem.getName(), e.getMessage());
            return ValidationResult.failure("Regex validation failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean supports(String validationType) {
        return "regex".equals(validationType) || "pattern".equals(validationType);
    }
    
    @Override
    public int getOrder() {
        return 300;
    }
    
    private Pattern getCompiledPattern(String patternStr, Map<String, Object> validation) {
        // 考虑标志位的缓存键
        String cacheKey = buildCacheKey(patternStr, validation);
        
        return patternCache.computeIfAbsent(cacheKey, key -> {
            int flags = parseFlags(validation);
            return Pattern.compile(patternStr, flags);
        });
    }
    
    private String buildCacheKey(String pattern, Map<String, Object> validation) {
        if (!validation.containsKey(FLAGS_KEY)) {
            return pattern;
        }
        return pattern + "|flags:" + validation.get(FLAGS_KEY).toString();
    }
    
    private int parseFlags(Map<String, Object> validation) {
        if (!validation.containsKey(FLAGS_KEY)) {
            return 0;
        }
        
        Object flagsObj = validation.get(FLAGS_KEY);
        if (flagsObj instanceof Number) {
            return ((Number) flagsObj).intValue();
        }
        
        if (flagsObj instanceof String) {
            String flagsStr = (String) flagsObj;
            int flags = 0;
            
            if (flagsStr.contains("CASE_INSENSITIVE") || flagsStr.contains("i")) {
                flags |= Pattern.CASE_INSENSITIVE;
            }
            if (flagsStr.contains("MULTILINE") || flagsStr.contains("m")) {
                flags |= Pattern.MULTILINE;
            }
            if (flagsStr.contains("DOTALL") || flagsStr.contains("s")) {
                flags |= Pattern.DOTALL;
            }
            
            return flags;
        }
        
        return 0;
    }
}