package io.github.tml.mosaic.install.domian.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cube配置信息容器
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CubeConfigInfo {
    
    @JsonProperty("cubeId")
    private String cubeId;
    
    @JsonProperty("config")
    private List<CubeConfigItem> config;
    
    /**
     * 根据配置项名称获取配置项
     */
    public CubeConfigItem getConfigItem(String name) {
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
    public List<CubeConfigItem> getRequiredConfigItems() {
        return Optional.ofNullable(config)
                .orElse(Collections.emptyList())
                .stream()
                .filter(CubeConfigItem::isRequired)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据类型获取配置项列表
     */
    public List<CubeConfigItem> getConfigItemsByType(String type) {
        return Optional.ofNullable(config)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> Objects.equals(item.getType(), type))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取配置项的名称列表
     */
    public List<String> getConfigItemNames() {
        return Optional.ofNullable(config)
                .orElse(Collections.emptyList())
                .stream()
                .map(CubeConfigItem::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    /**
     * 验证配置完整性
     */
    public List<String> validateRequiredConfigs(Map<String, Object> actualValues) {
        List<String> missingConfigs = new ArrayList<>();
        
        getRequiredConfigItems().forEach(requiredItem -> {
            String itemName = requiredItem.getName();
            if (!actualValues.containsKey(itemName) || 
                !requiredItem.validateValue(actualValues.get(itemName))) {
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
    public int getConfigItemCount() {
        return Optional.ofNullable(config)
                .map(List::size)
                .orElse(0);
    }
}