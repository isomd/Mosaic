package io.github.tml.mosaic.core.factory.config;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.factory.definition.CubeDefinition;

/**
 * 描述: Cube实例化策略
 * @author suifeng
 * 日期: 2025/6/6
 */
public interface InstantiationStrategy {

    Cube instantiate(CubeDefinition cubeDefinition, GUID cubeId, Object[] args) throws CubeException;
}
