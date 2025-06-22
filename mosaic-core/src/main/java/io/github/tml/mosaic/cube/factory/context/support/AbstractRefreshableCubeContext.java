package io.github.tml.mosaic.cube.factory.context.support;

import com.alibaba.fastjson.JSONObject;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.context.ConfigurableCubeContext;
import io.github.tml.mosaic.cube.factory.support.DefaultDefinitionListableCubeFactory;
import io.github.tml.mosaic.cube.factory.support.ListableCubeFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述: 可刷新的Cube上下文抽象实现
 * 负责管理配置信息和Cube工厂的生命周期
 *
 * @author suifeng
 * 日期: 2025/6/7
 */
@Slf4j
public abstract class AbstractRefreshableCubeContext extends AbstractCubeContext implements ConfigurableCubeContext {

    /**
     * 配置信息存储容器
     * Key: YAML文件中的顶级配置键
     * Value: 对应配置键下的完整配置对象
     */
    private final Map<String, JSONObject> configurationMap = new ConcurrentHashMap<>();

    /**
     * Cube工厂实例
     */
    private final DefaultDefinitionListableCubeFactory cubeFactory;

    /**
     * 构造函数，初始化Cube工厂
     */
    public AbstractRefreshableCubeContext() {
        this.cubeFactory = createCubeFactory();
        log.info("AbstractRefreshableCubeContext initialized with factory: {}", cubeFactory.getClass().getSimpleName());
    }

    /**
     * 刷新上下文，包括配置加载和Bean预实例化
     *
     * @throws CubeException 刷新过程中的异常
     */
    @Override
    public void refresh() throws CubeException {
        log.info("Starting context refresh process...");

        try {
            // 1. 获取 CubeFactory
            ListableCubeFactory cubeFactory = getBeanFactory();
            log.debug("Retrieved CubeFactory: {}", cubeFactory.getClass().getSimpleName());

            // 2. 刷新Cube的Config配置资源
            refreshConfigurationResources();
            log.info("Configuration resources refreshed successfully");

            // 3. 提前实例化单例Bean对象
            cubeFactory.preInstantiateSingletons();
            log.info("Singleton beans pre-instantiated successfully");

        } catch (Exception e) {
            log.error("Failed to refresh context", e);
            throw new CubeException("Context refresh failed: " + e.getMessage(), e);
        }

        log.info("Context refresh completed successfully");
    }

    /**
     * 刷新配置资源的抽象方法
     * 子类需要实现具体的配置加载逻辑
     *
     * @throws CubeException 配置刷新异常
     */
    protected abstract void refreshConfigurationResources() throws CubeException;

    /**
     * 创建Cube工厂实例
     *
     * @return DefaultDefinitionListableCubeFactory实例
     */
    private DefaultDefinitionListableCubeFactory createCubeFactory() {
        return new DefaultDefinitionListableCubeFactory();
    }

    /**
     * 获取Cube工厂实例
     *
     * @return ListableCubeFactory实例
     */
    @Override
    protected ListableCubeFactory getBeanFactory() {
        return cubeFactory;
    }

    /**
     * 更新配置信息
     *
     * @param configKey 配置键
     * @param configValue 配置值
     */
    protected void updateConfiguration(String configKey, JSONObject configValue) {
        if (configKey == null || configKey.trim().isEmpty()) {
            log.warn("Attempted to update configuration with null or empty key");
            return;
        }

        if (configValue == null) {
            log.warn("Attempted to update configuration with null value for key: {}", configKey);
            configValue = new JSONObject();
        }

        configurationMap.put(configKey, configValue);
        log.debug("Configuration updated for key: {}", configKey);
    }

    /**
     * 批量更新配置信息
     *
     * @param configurations 配置映射
     */
    protected void updateConfigurations(Map<String, JSONObject> configurations) {
        if (configurations == null || configurations.isEmpty()) {
            log.warn("Attempted to update configurations with null or empty map");
            return;
        }

        configurations.forEach(this::updateConfiguration);
        log.info("Batch configuration update completed, {} items updated", configurations.size());
    }

    /**
     * 获取指定键的配置信息
     *
     * @param configKey 配置键
     * @return 配置对象，如果不存在则返回空的JSONObject
     */
    public JSONObject getConfiguration(String configKey) {
        if (configKey == null || configKey.trim().isEmpty()) {
            log.warn("Attempted to get configuration with null or empty key");
            return new JSONObject();
        }

        JSONObject config = configurationMap.get(configKey);
        return config != null ? config : new JSONObject();
    }

    /**
     * 获取所有配置信息的只读视图
     *
     * @return 配置信息的不可变映射
     */
    public Map<String, JSONObject> getAllConfigurations() {
        return Collections.unmodifiableMap(configurationMap);
    }

    /**
     * 清空所有配置信息
     */
    protected void clearConfigurations() {
        configurationMap.clear();
        log.info("All configurations cleared");
    }

    /**
     * 检查是否存在指定的配置键
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    public boolean hasConfiguration(String configKey) {
        return configKey != null && configurationMap.containsKey(configKey);
    }

    /**
     * 获取配置数量
     *
     * @return 配置项数量
     */
    public int getConfigurationCount() {
        return configurationMap.size();
    }
}