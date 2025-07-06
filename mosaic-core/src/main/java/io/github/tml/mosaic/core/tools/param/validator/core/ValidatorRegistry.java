package io.github.tml.mosaic.core.tools.param.validator.core;

import io.github.tml.mosaic.core.tools.param.validator.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 校验器注册管理器
 */
@Slf4j
public class ValidatorRegistry {
    
    private static final ValidatorRegistry INSTANCE = new ValidatorRegistry();
    
    private final Map<String, List<ConfigValidator>> validatorMap = new ConcurrentHashMap<>();
    private final List<ConfigValidator> allValidators = new ArrayList<>();
    private volatile boolean initialized = false;
    
    private ValidatorRegistry() {}
    
    public static ValidatorRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * 注册校验器
     */
    public synchronized void registerValidator(ConfigValidator validator) {
        if (validator == null) {
            throw new IllegalArgumentException("Validator cannot be null");
        }
        
        allValidators.add(validator);
        
        // 按优先级排序
        allValidators.sort(Comparator.comparingInt(ConfigValidator::getOrder));
        
        // 重建索引
        rebuildValidatorIndex();
        
        log.debug("Registered validator: {} with order: {}", 
            validator.getName(), validator.getOrder());
    }
    
    /**
     * 批量注册校验器
     */
    public synchronized void registerValidators(ConfigValidator... validators) {
        for (ConfigValidator validator : validators) {
            registerValidator(validator);
        }
    }
    
    /**
     * 获取支持指定校验类型的校验器列表
     */
    public List<ConfigValidator> getValidators(String validationType) {
        return validatorMap.getOrDefault(validationType, Collections.emptyList());
    }
    
    /**
     * 获取所有校验器
     */
    public List<ConfigValidator> getAllValidators() {
        return new ArrayList<>(allValidators);
    }
    
    /**
     * 初始化默认校验器
     */
    public synchronized void initializeDefaultValidators() {
        if (initialized) {
            return;
        }
        
        registerValidator(new EnumValidator());
        registerValidator(new RangeValidator());
        registerValidator(new RegexValidator());
        registerValidator(new LengthValidator());
        registerValidator(new NotNullValidator());
        
        initialized = true;
        log.info("[Cube] has benn initialized {} default cube config validators", allValidators.size());
    }
    
    /**
     * 清空所有校验器
     */
    public synchronized void clear() {
        allValidators.clear();
        validatorMap.clear();
        initialized = false;
    }
    
    /**
     * 重建校验器索引
     */
    private void rebuildValidatorIndex() {
        validatorMap.clear();
        
        for (ConfigValidator validator : allValidators) {
            // 这里需要一种方式来获取validator支持的所有类型
            // 由于supports方法需要参数，我们采用预定义的方式
            String[] supportedTypes = getSupportedTypes(validator);
            for (String type : supportedTypes) {
                validatorMap.computeIfAbsent(type, k -> new ArrayList<>()).add(validator);
            }
        }
    }
    
    /**
     * 获取校验器支持的类型（可以通过注解或者约定来实现）
     */
    private String[] getSupportedTypes(ConfigValidator validator) {
        // 简单的基于类名的约定
        String className = validator.getClass().getSimpleName();
        if (className.contains("Enum")) {
            return new String[]{"enum"};
        } else if (className.contains("Range")) {
            return new String[]{"range", "min", "max"};
        } else if (className.contains("Regex")) {
            return new String[]{"regex", "pattern"};
        } else if (className.contains("Length")) {
            return new String[]{"length", "minLength", "maxLength"};
        } else if (className.contains("NotNull")) {
            return new String[]{"notNull", "required"};
        }
        return new String[0];
    }
}