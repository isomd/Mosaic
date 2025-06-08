package io.github.tml.mosaic.core.factory;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.context.json.JsonCubeInstallationItemReader;
import io.github.tml.mosaic.core.factory.context.support.AbstractResourcesLoaderCubeContext;

/**
 * 描述: 核心应用上下文实现类
 * @author suifeng
 * 日期: 2025/6/7
 */
public class ClassPathJsonCubeContext extends AbstractResourcesLoaderCubeContext {

    private final String[] configLocations;

    public ClassPathJsonCubeContext() {
        this(new String[0]);
    }

    /**
     * 从 JSON 中加载 CubeDefinition，并刷新上下文
     */
    public ClassPathJsonCubeContext(String configLocations) throws CubeException {
        this(new String[]{configLocations});
    }

    public ClassPathJsonCubeContext(String[] configLocations) throws CubeException {
        super();
        this.configLocations = configLocations;
        this.cubeInstallationItemReader = new JsonCubeInstallationItemReader();
    }

    @Override
    protected String[] getConfigLocations() {
        return configLocations;
    }
}
