package io.github.tml.mosaic.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 描述: 马赛克插件的配置
 * @author suifeng
 * 日期: 2025/6/15
 */
@Data
@Component
@ConfigurationProperties(prefix = "mosaic.plugin")
public class MosaicPluginProperties {

    /**
     * 插件存储目录
     * 默认使用系统临时目录下的mosaic/plugins子目录
     */
    private String storagePath = System.getProperty("java.io.tmpdir") + "/mosaic/plugins";



}