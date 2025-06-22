package io.github.tml.mosaic.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * JAR包安装状态枚举
 */
@Getter
@RequiredArgsConstructor
public enum JarInstallStatus {
    
    /**
     * 未安装 - JAR包已上传但未注册Cube定义
     */
    UNINSTALLED("UNINSTALLED", "未安装"),
    
    /**
     * 已安装 - JAR包已注册Cube定义
     */
    INSTALLED("INSTALLED", "已安装"),
    
    /**
     * 安装失败 - JAR包注册Cube定义时发生错误
     */
    INSTALL_FAILED("INSTALL_FAILED", "安装失败");
    
    private final String code;
    private final String description;
}