package io.github.tml.mosaic.cube.factory.context.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.context.ConfigurableCubeContext;

import io.github.tml.mosaic.cube.factory.support.DefaultDefinitionListableCubeFactory;
import io.github.tml.mosaic.cube.factory.support.ListableCubeFactory;

/**
 * 描述: 获取Cube工厂和加载资源
 * @author suifeng
 * 日期: 2025/6/7
 */
public abstract class AbstractRefreshableCubeContext extends AbstractCubeContext implements ConfigurableCubeContext {

    private DefaultDefinitionListableCubeFactory cubeFactory;

    public AbstractRefreshableCubeContext() {
        cubeFactory = createBeanFactory();
    }

    @Override
    public void refresh() throws CubeException {
        // 1. 创建 CubeFactory，并加载 CubeDefinition
        refreshCubeFactory();

        // 2. 获取 CubeFactory
        ListableCubeFactory cubeFactory = getBeanFactory();

        // 3. 提前实例化单例Bean对象
        cubeFactory.preInstantiateSingletons();
    }

    protected void refreshCubeFactory() throws CubeException {
        loadCubeDefinitions(cubeFactory);
    }

    private DefaultDefinitionListableCubeFactory createBeanFactory() {
        return new DefaultDefinitionListableCubeFactory();
    }

    @Override
    protected ListableCubeFactory getBeanFactory() {
        return cubeFactory;
    }
}
