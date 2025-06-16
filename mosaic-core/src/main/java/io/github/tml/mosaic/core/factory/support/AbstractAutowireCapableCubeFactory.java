package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.*;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.factory.config.InstantiationStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述: 第一步：用于实例化Cube的类
 *
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public abstract class AbstractAutowireCapableCubeFactory extends AbstractCubeFactory {

    private final InstantiationStrategy instantiationStrategy = new DefaultInstantiationStrategy();

    @Override
    protected Cube createCube(GUID cubeId, CubeDefinition cubeDefinition, Object[] args) throws CubeException {
        Cube cube = null;
        try {
            cube = instantiationStrategy.instantiate(cubeDefinition, cubeId, args);
            // 给 Cube 填充属性
            applyPropertyValues(cube, cubeDefinition);
            // 执行 Cube 的初始化方法
            cube = initializeCube(cube, cubeDefinition);
        } catch (Exception e) {
            throw new CubeException("Instantiation of cube failed", e);
        }

        // 根据Cube模式进行不同的注册策略
        String model = cubeDefinition.getModel();
        if ("singleton".equals(model)) {
            // 单例模式：注册到单例池
            addSingleton(cubeId, cube);
            log.info("✓ singleton Cube register | CubeId: {}", cubeId);
        } else {
            // 多例模式：注册到管理器
            GUID instanceId = registerCube(cubeId, cube);
            log.info("✓ property Cube register | CubeId: {} | instanceId: {}", cubeId, instanceId);
        }
        return cube;
    }

    protected abstract Cube initializeCube(Cube cube, CubeDefinition cubeDefinition);
    protected abstract void applyPropertyValues(Cube cube, CubeDefinition cubeDefinition);
}
