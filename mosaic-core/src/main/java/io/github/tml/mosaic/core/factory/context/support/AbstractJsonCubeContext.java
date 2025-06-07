package io.github.tml.mosaic.core.factory.context.support;

import io.github.tml.mosaic.factory.context.json.JsonCubeDefinitionReader;
import io.github.tml.mosaic.factory.support.DefaultDefinitionListableCubeFactory;
import io.github.tml.mosaic.factory.support.ListableCubeFactory;
import io.github.tml.mosaic.install.support.registry.InstallerRegistry;

/**
 * 描述: 上下文中对配置信息的加载
 * @author suifeng
 * 日期: 2025/6/7
 */
public abstract class AbstractJsonCubeContext extends AbstractRefreshableCubeContext {

    @Override
    protected void loadCubeDefinitions(ListableCubeFactory cubeFactory) {
        JsonCubeDefinitionReader cubeDefinitionReader = new JsonCubeDefinitionReader(cubeFactory, getInstallerRegistry());
        String[] configLocations = getConfigLocations();
        if (null != configLocations){
            cubeDefinitionReader.loadCubeDefinitions(configLocations);
        }
    }
    protected abstract String[] getConfigLocations();
    protected abstract InstallerRegistry getInstallerRegistry();
}
