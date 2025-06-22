package io.github.tml.mosaic.cube.factory.context.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.context.ConfigurableCubeContext;
import io.github.tml.mosaic.cube.factory.support.DefaultDefinitionListableCubeFactory;
import io.github.tml.mosaic.cube.factory.support.ListableCubeFactory;
import lombok.extern.slf4j.Slf4j;

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
//            cubeFactory.preInstantiateSingletons();
//            log.info("Singleton beans pre-instantiated successfully");

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
}