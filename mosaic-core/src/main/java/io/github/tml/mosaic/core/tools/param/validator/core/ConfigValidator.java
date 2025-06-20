package io.github.tml.mosaic.core.tools.param.validator.core;

import io.github.tml.mosaic.core.tools.param.ConfigItem;

/**
 * 描述: 配置值校验器接口
 * @author suifeng
 * 日期: 2025/6/18
 */
public interface ConfigValidator {
    
    /**
     * 校验配置值
     * @param value 待校验的值
     * @param configItem 配置项定义
     * @return 校验结果
     */
    ValidationResult validate(Object value, ConfigItem configItem);
    
    /**
     * 判断是否支持该类型的校验
     * @param validationType 校验类型
     * @return 是否支持
     */
    boolean supports(String validationType);
    
    /**
     * 获取校验器优先级（数值越小优先级越高）
     */
    default int getOrder() {
        return Integer.MAX_VALUE;
    }
    
    /**
     * 获取校验器名称
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}