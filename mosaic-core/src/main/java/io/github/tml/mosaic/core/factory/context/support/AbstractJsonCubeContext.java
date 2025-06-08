package io.github.tml.mosaic.core.factory.context.support;

import io.github.tml.mosaic.core.factory.context.json.JsonCubeInstallationItemReader;
import io.github.tml.mosaic.core.factory.support.ListableCubeFactory;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;

/**
 * 描述: 上下文中对配置信息的加载
 * @author suifeng
 * 日期: 2025/6/7
 */
public abstract class AbstractJsonCubeContext extends AbstractRefreshableCubeContext {

    @Override
    protected void loadCubeDefinitions(ListableCubeFactory cubeFactory) {
        JsonCubeInstallationItemReader cubeDefinitionReader = new JsonCubeInstallationItemReader(cubeFactory);
        String[] configLocations = getConfigLocations();
        if (null != configLocations){
            cubeDefinitionReader.loadCubeInstallationItem(configLocations);
        }
    }
    protected abstract String[] getConfigLocations();
    protected abstract ResourceFileAdapterRegistry getAdapterRegistry();
}
