package io.github.tml.mosaic.cube.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.CubeFactory;

import java.util.Objects;

/**
 * 描述: Cube工厂抽象类（定义getCube的模板方法）
 * @author suifeng
 * 日期: 2025/6/6
 */
public abstract class AbstractCubeFactory extends DefaultSingletonCubeRegistry implements CubeFactory {

    /**
     * 实现getCube方法，定义主流程
     */
    @Override
    public Cube getCube(GUID cubeId, Object[] args) throws CubeException {

        // 1. 获取Cube实例
        Cube singletonCube = getSingleton(cubeId);
        if (singletonCube != null) {
            return singletonCube;
        }

        // 2. 创建新实例
        CubeDefinition cubeDefinition = getCubeDefinition(cubeId);
        if (Objects.isNull(cubeDefinition)) {
            return null;
        }

        return createCube(cubeId, cubeDefinition, args);
    }

    @Override
    public Cube getCube(GUID cubeId) throws CubeException {
        return getCube(cubeId, null);
    }

    // 获取Cube定义，由子类实现
    protected abstract CubeDefinition getCubeDefinition(GUID cubeId) throws CubeException;

    // 创建Cube，由子类实现
    protected abstract Cube createCube(GUID cubeId, CubeDefinition cubeDefinition, Object[] args) throws CubeException;
}
