package io.github.tml.mosaic.cube.factory.context.support;

import com.alibaba.fastjson.JSONObject;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.config.ConfigReader;
import io.github.tml.mosaic.cube.config.YamlConfigReader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 描述: 配置加载器Cube上下文抽象实现
 * 负责从配置文件中加载配置信息
 *
 * @author suifeng
 * 日期: 2025/6/7
 */
@Getter
@Slf4j
public abstract class AbstractConfigLoaderCubeContext extends AbstractRefreshableCubeContext {

    /**
     * 配置读取器
     *  获取当前配置读取器
     */
    private ConfigReader configReader;

    /**
     * 构造函数，初始化配置读取器
     */
    public AbstractConfigLoaderCubeContext() {
        super();
        this.configReader = createDefaultConfigReader();
        log.info("AbstractConfigLoaderCubeContext initialized with default config reader");
    }

    /**
     * 刷新配置资源的具体实现
     * 从指定的配置位置加载配置信息
     *
     * @throws CubeException 配置加载异常
     */
    @Override
    protected void refreshConfigurationResources() throws CubeException {
        log.info("Starting configuration resources refresh...");

        try {
            // 清空现有配置
            clearConfigurations();

            // 获取配置文件位置
            String[] configLocations = getConfigLocations();

            if (configLocations == null || configLocations.length == 0) {
                log.warn("No configuration locations specified, using default configuration");
                loadDefaultConfiguration();
                return;
            }

            log.info("Loading configurations from {} locations: {}",
                    configLocations.length, Arrays.toString(configLocations));

            // 逐个加载配置文件
            for (String configLocation : configLocations) {
                if (configLocation == null || configLocation.trim().isEmpty()) {
                    log.warn("Skipping null or empty configuration location");
                    continue;
                }

                loadConfigurationFromLocation(configLocation.trim());
            }

            log.info("Configuration resources refresh completed successfully, {} configurations loaded",
                    getConfigurationCount());

        } catch (Exception e) {
            log.error("Failed to refresh configuration resources", e);
            throw new CubeException("Configuration refresh failed: " + e.getMessage(), e);
        }
    }

    /**
     * 从指定位置加载配置
     *
     * @param configLocation 配置文件位置
     * @throws CubeException 配置加载异常
     */
    private void loadConfigurationFromLocation(String configLocation) throws CubeException {
        log.debug("Loading configuration from location: {}", configLocation);

        try {
            // 设置配置文件路径
            if (configReader instanceof YamlConfigReader) {
                ((YamlConfigReader) configReader).setYamlFilePath(configLocation);
            }

            // 读取配置
            JSONObject configData = configReader.readConfig();

            if (configData == null || configData.isEmpty()) {
                log.warn("No configuration data found in location: {}", configLocation);
                return;
            }

            // 解析并存储配置
            parseAndStoreConfiguration(configData, configLocation);

            log.info("Successfully loaded configuration from: {}", configLocation);

        } catch (Exception e) {
            log.error("Failed to load configuration from location: {}", configLocation, e);
            throw new CubeException("Failed to load configuration from: " + configLocation, e);
        }
    }

    /**
     * 解析并存储配置数据
     * 将配置数据按顶级键分组存储
     *
     * @param configData 配置数据
     * @param configLocation 配置文件位置（用于日志）
     */
    private void parseAndStoreConfiguration(JSONObject configData, String configLocation) {
        log.debug("Parsing configuration data from: {}", configLocation);

        for (Map.Entry<String, Object> entry : configData.entrySet()) {
            String topLevelKey = entry.getKey();
            Object value = entry.getValue();

            if (topLevelKey == null || topLevelKey.trim().isEmpty()) {
                log.warn("Skipping configuration entry with null or empty key from: {}", configLocation);
                continue;
            }

            JSONObject configSection;
            if (value instanceof Map) {
                configSection = new JSONObject((Map<String, Object>) value);
            } else {
                // 如果不是Map类型，创建一个包装对象
                configSection = new JSONObject();
                configSection.put("value", value);
            }

            updateConfiguration(topLevelKey, configSection);
            log.debug("Stored configuration section: {} from location: {}", topLevelKey, configLocation);
        }
    }

    /**
     * 加载默认配置
     * 当没有指定配置位置时使用
     */
    private void loadDefaultConfiguration() {
        log.info("Loading default configuration...");

        try {
            JSONObject defaultConfig = configReader.readConfig();
            if (defaultConfig != null && !defaultConfig.isEmpty()) {
                parseAndStoreConfiguration(defaultConfig, "default");
                log.info("Default configuration loaded successfully");
            } else {
                log.warn("No default configuration available");
            }
        } catch (Exception e) {
            log.error("Failed to load default configuration", e);
        }
    }

    /**
     * 创建默认配置读取器
     *
     * @return ConfigReader实例
     */
    protected ConfigReader createDefaultConfigReader() {
        return new YamlConfigReader();
    }

    /**
     * 设置配置读取器
     *
     * @param configReader 配置读取器
     */
    public void setConfigReader(ConfigReader configReader) {
        this.configReader = Objects.requireNonNull(configReader, "ConfigReader cannot be null");
        log.info("ConfigReader updated to: {}", configReader.getClass().getSimpleName());
    }

    /**
     * 获取配置文件位置的抽象方法
     * 子类需要实现此方法来提供配置文件路径
     *
     * @return 配置文件位置数组
     */
    protected abstract String[] getConfigLocations();
}