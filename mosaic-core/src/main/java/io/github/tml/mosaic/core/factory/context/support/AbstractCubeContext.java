package io.github.tml.mosaic.core.factory.context.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.core.factory.support.ListableCubeFactory;

/**
 * 描述: Cube上下文抽象类实现
 * @author suifeng
 * 日期: 2025/6/7
 */
public abstract class AbstractCubeContext extends DefaultResourceLoader implements CubeContext {

    protected abstract void refreshCubeFactory() throws CubeException;

    protected abstract ListableCubeFactory getBeanFactory();

    @Override
    public Cube getCube(GUID cubeId) throws CubeException {
        return getBeanFactory().getCube(cubeId);
    }

    @Override
    public Cube getCube(GUID cubeId, Object... args) throws CubeException {
        return getBeanFactory().getCube(cubeId, args);
    }

    public void registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition){
        getBeanFactory().registerCubeDefinition(cubeId, cubeDefinition);
    }

    protected abstract void loadCubeDefinitions(ListableCubeFactory cubeFactory);
}
