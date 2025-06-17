package io.github.tml.mosaic.cube.factory.context.support;

import io.github.tml.mosaic.cube.factory.support.ListableCubeFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述: 上下文中对配置信息的加载
 *
 * @author suifeng
 * 日期: 2025/6/7
 */
@Slf4j
public abstract class AbstractResourcesLoaderCubeContext extends AbstractRefreshableCubeContext {

    @Override
    protected void loadCubeDefinitions(ListableCubeFactory cubeFactory) {
        String[] configLocations = getConfigLocations();
    }

    protected abstract String[] getConfigLocations();
}
