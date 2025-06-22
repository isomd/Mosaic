package io.github.tml.mosaic.core.tools.param;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.UniqueEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述: 可配置实体基类
 * @author suifeng
 * 日期: 2025/6/18
 */
@Data
@Slf4j
public abstract class ConfigurableEntity extends UniqueEntity {

    /**
     * 配置定义信息
     */
    private ConfigInfo configInfo;

    /**
     * 配置值存储
     */
    private final Map<String, Object> configValues = new ConcurrentHashMap<>();

    public ConfigurableEntity(GUID id) {
        super(id);
    }

    /**
     * 批量设置配置值（带校验）
     */
    public void setConfigs(Map<String, Object> configs) throws CubeException {
        // 确保configs不为null
        Map<String, Object> safeConfigs = configs != null ? configs : new HashMap<>();

        try {
            // 1. 清空现有配置
            configValues.clear();

            // 2. 校验配置（包括空配置的校验）
            validateConfigs(safeConfigs);

            // 3. 存储用户提供的配置
            if (!safeConfigs.isEmpty()) {
                configValues.putAll(safeConfigs);
            }

            // 4. 设置默认值（始终执行）
            setDefaultValues();

            log.debug("✓ Config processing completed | Provided: {}, Final: {}", safeConfigs.size(), configValues.size());

        } catch (CubeException e) {
            throw e;
        } catch (Exception e) {
            throw new CubeException("Config processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * 获取配置值
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String name, Class<T> type) {
        Object value = configValues.get(name);
        if (value == null) {
            return getDefaultValue(name, type);
        }
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
     * 检查配置是否存在
     */
    public boolean hasConfig(String name) {
        return configValues.containsKey(name) || hasDefaultConfig(name);
    }

    /**
     * 获取所有配置（只读）
     */
    public Map<String, Object> getAllConfigs() {
        return Collections.unmodifiableMap(configValues);
    }

    /**
     * 校验配置
     */
    private void validateConfigs(Map<String, Object> configs) throws CubeException {
        if (configInfo == null) {
            return;
        }

        // 校验必填项
        List<String> missing = configInfo.validateRequiredConfigs(configs);
        if (!missing.isEmpty()) {
            throw new CubeException("Missing required configs: " + String.join(", ", missing));
        }

        // 校验每个配置项
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            ConfigItem item = configInfo.getConfigItem(name);
            
            if (item != null && !item.getValidationResult(value).isValid()) {
                throw new CubeException("Invalid config value: " + name + " = " + value);
            }
        }
    }

    /**
     * 设置默认值
     */
    private void setDefaultValues() {
        if (configInfo == null || !configInfo.hasConfigItems()) {
            return;
        }

        configInfo.getConfig().forEach(item -> {
            String name = item.getName();
            if (!configValues.containsKey(name) && item.getDefaultValue() != null) {
                configValues.put(name, item.getDefaultValue());
            }
        });
    }

    /**
     * 获取默认值
     */
    private <T> T getDefaultValue(String name, Class<T> type) {
        if (configInfo == null) {
            return null;
        }
        ConfigItem item = configInfo.getConfigItem(name);
        return item != null ? item.getDefaultValue(type) : null;
    }

    /**
     * 检查是否有默认配置
     */
    private boolean hasDefaultConfig(String name) {
        if (configInfo == null) {
            return false;
        }
        ConfigItem item = configInfo.getConfigItem(name);
        return item != null && item.getDefaultValue() != null;
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
            log.warn("Type conversion failed: {} -> {}", value, type.getSimpleName());
            return null;
        }
    }
}