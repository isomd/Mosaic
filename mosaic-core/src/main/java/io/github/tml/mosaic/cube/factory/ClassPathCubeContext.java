package io.github.tml.mosaic.cube.factory;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.context.support.AbstractResourcesLoaderCubeContext;

/**
 * 描述: 核心应用上下文实现类
 * @author suifeng
 * 日期: 2025/6/7
 */
public class ClassPathCubeContext extends AbstractResourcesLoaderCubeContext {

    private final String[] configLocations;

    public ClassPathCubeContext() {
        this(new String[0]);
    }

    public ClassPathCubeContext(String configLocations) throws CubeException {
        this(new String[]{configLocations});
    }

    public ClassPathCubeContext(String[] configLocations) throws CubeException {
        super();
        this.configLocations = configLocations;
    }

    @Override
    public String[] getConfigLocations() {
        return configLocations;
    }
}
