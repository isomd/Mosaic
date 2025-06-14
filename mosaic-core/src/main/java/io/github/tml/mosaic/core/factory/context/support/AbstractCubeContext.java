package io.github.tml.mosaic.core.factory.context.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.factory.support.ListableCubeFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public GUID registerCube(GUID cubeId, Cube cube) {
        return getBeanFactory().registerCube(cubeId, cube);
    }

    @Override
    public boolean removeCubeByInstanceId(GUID instanceId) {
        return getBeanFactory().removeCubeByInstanceId(instanceId);
    }

    @Override
    public int removeCubesByCubeId(GUID cubeId) {
        return getBeanFactory().removeCubesByCubeId(cubeId);
    }

    @Override
    public Cube getCubeByInstanceId(GUID instanceId) {
        return getBeanFactory().getCubeByInstanceId(instanceId);
    }

    @Override
    public List<Cube> getCubesByCubeId(GUID cubeId) {
        return getBeanFactory().getCubesByCubeId(cubeId);
    }

    @Override
    public Cube getFirstCubeByCubeId(GUID cubeId) {
        return getBeanFactory().getFirstCubeByCubeId(cubeId);
    }

    @Override
    public boolean containsCubeId(GUID cubeId) {
        return getBeanFactory().containsCubeId(cubeId);
    }

    @Override
    public boolean containsInstanceId(GUID instanceId) {
        return getBeanFactory().containsInstanceId(instanceId);
    }

    @Override
    public Set<GUID> getAllCubeIds() {
        return getBeanFactory().getAllCubeIds();
    }

    @Override
    public Set<GUID> getAllInstanceIds() {
        return getBeanFactory().getAllInstanceIds();
    }

    @Override
    public int getCubeInstanceCount(GUID cubeId) {
        return getBeanFactory().getCubeInstanceCount(cubeId);
    }

    @Override
    public int getTotalInstanceCount() {
        return getBeanFactory().getTotalInstanceCount();
    }

    @Override
    public Map<GUID, Cube> getAllCubeInstances() {
        return getBeanFactory().getAllCubeInstances();
    }

    @Override
    public void clear() {
        getBeanFactory().clear();
    }

    public void registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition) {
        getBeanFactory().registerCubeDefinition(cubeId, cubeDefinition);
    }

    protected abstract void loadCubeDefinitions(ListableCubeFactory cubeFactory);
}
