package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.factory.config.SingletonCubeRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述: 单例Cube注册默认实现类
 * @author suifeng
 * 日期: 2025/6/6
 */
public class DefaultSingletonCubeRegistry implements SingletonCubeRegistry {

    private final Map<GUID, Cube> singletonCubes = new HashMap<>();

    @Override
    public Cube getSingleton(GUID cubeId) {
        return singletonCubes.get(cubeId);
    }

    protected void addSingleton(GUID cubeId, Cube cube) {
        singletonCubes.put(cubeId, cube);
    }
}
