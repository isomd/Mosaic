package io.github.tml.mosaic.core.factory.context;

import io.github.tml.mosaic.core.factory.CubeFactory;
import io.github.tml.mosaic.core.factory.config.CubeManager;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUID;

/**
 * 描述: Cube上下文接口
 * @author suifeng
 * 日期: 2025/6/7
 */
public interface CubeContext extends CubeFactory, CubeManager {

    void registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition);
}
