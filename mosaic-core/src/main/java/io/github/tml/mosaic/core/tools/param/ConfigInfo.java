package io.github.tml.mosaic.core.tools.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述: 配置信息容器
 * @author suifeng
 * 日期: 2025/6/18
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigInfo {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("config")
    private List<ConfigItem> config;
    
    /**
     * 根据配置项名称获取配置项
     */
    public ConfigItem getConfigItem(String name) {
        return Optional.ofNullable(config)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> Objects.equals(item.getName(), name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取所有必填配置项
     */
    public List<ConfigItem> requiredConfigItems() {
        return Optional.ofNullable(config)
                .orElse(Collections.emptyList())
                .stream()
                .filter(ConfigItem::isRequired)
                .collect(Collectors.toList());
    }

    /**
     * 验证配置完整性
     */
    public List<String> validateRequiredConfigs(Map<String, Object> actualValues) {
        List<String> missingConfigs = new ArrayList<>();

        requiredConfigItems().forEach(requiredItem -> {
            String itemName = requiredItem.getName();
            if (!actualValues.containsKey(itemName) || !requiredItem.validateValue(actualValues.get(itemName))) {
                missingConfigs.add(itemName);
            }
        });
        return missingConfigs;
    }
    
    /**
     * 检查是否有配置项
     */
    public boolean hasConfigItems() {
        return config != null && !config.isEmpty();
    }
    
    /**
     * 获取配置项数量
     */
    public int configItemCount() {
        return Optional.ofNullable(config).map(List::size).orElse(0);
    }
}