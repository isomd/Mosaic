package io.github.tml.mosaic.cube.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.config.InstantiationStrategy;
import io.github.tml.mosaic.cube.external.MosaicCube;

/**
 * 描述: 默认的Cube实例化策略
 * @author suifeng
 * 日期: 2025/6/6
 */
public class DefaultInstantiationStrategy implements InstantiationStrategy {

    @Override
    public Cube instantiate(CubeDefinition cubeDefinition, GUID cubeId, Object[] args) throws CubeException {
        try {
            return new Cube(cubeId);
        } catch (Exception e) {
            throw new CubeException("cube init error: " + cubeDefinition.getClassName(), e);
        }
    }
}
