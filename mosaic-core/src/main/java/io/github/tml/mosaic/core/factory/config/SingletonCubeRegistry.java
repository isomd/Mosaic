package io.github.tml.mosaic.core.factory.config;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;

/**
 * 描述: 单例Cube注册接口
 * @author suifeng
 * 日期: 2025/6/6
 */
public interface SingletonCubeRegistry {

    Cube getSingleton(GUID cubeId);
}
