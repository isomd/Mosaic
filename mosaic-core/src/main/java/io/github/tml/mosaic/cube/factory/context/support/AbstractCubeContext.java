package io.github.tml.mosaic.cube.factory.context.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.support.ListableCubeFactory;

import java.util.List;
import java.util.Map;

/**
 * 描述: Cube上下文抽象类实现
 * @author suifeng
 * 日期: 2025/6/7
 */
public abstract class AbstractCubeContext implements CubeContext {

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
    @Override
    public List<CubeDefinition> getAllCubeDefinitions() {
        return getBeanFactory().getAllCubeDefinitions();
    }

    @Override
    public Map<GUID, CubeDefinition> getAllCubeDefinitionMap() {
        return getBeanFactory().getAllCubeDefinitionMap();
    }

    @Override
    public boolean containsCubeDefinition(String cubeId) {
        return getBeanFactory().containsCubeDefinition(cubeId);
    }

    @Override
    public void registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition) {
        getBeanFactory().registerCubeDefinition(cubeId, cubeDefinition);
    }

    @Override
    public void registerAllCubeDefinition(List<CubeDefinition> cubeDefinitionList) {
        for (CubeDefinition cubeDefinition : cubeDefinitionList) {
            registerCubeDefinition(new GUUID(cubeDefinition.getId()), cubeDefinition);
        }
    }

    protected abstract void loadCubeDefinitions(ListableCubeFactory cubeFactory);
}
