package io.github.tml.mosaic.core.factory.context.json;

import io.github.tml.mosaic.install.support.ResourceFileType;
import lombok.Data;

import java.util.Map;

/**
 * 描述: 安装配置类
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
public class InstallationItem {

    private ResourceFileType type;
    private String location;
    private Map<String, Object> properties;
}