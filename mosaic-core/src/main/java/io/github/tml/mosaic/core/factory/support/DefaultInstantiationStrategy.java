package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.factory.definition.CubeDefinition;
import io.github.tml.mosaic.factory.config.InstantiationStrategy;

import java.lang.reflect.Constructor;

/**
 * 描述: 默认的Cube实例化策略
 * @author suifeng
 * 日期: 2025/6/6
 */
public class DefaultInstantiationStrategy implements InstantiationStrategy {

    @Override
    public Cube instantiate(CubeDefinition cubeDefinition, GUID cubeId, Object[] args) throws CubeException {
        try {
            // 使用CubeDefinition的类加载器
            Class<?> clazz = cubeDefinition.getClassLoader().loadClass(cubeDefinition.getClassName());

            // 通过反射实例化
            Constructor<?> constructor = clazz.getConstructor();
            return (Cube) constructor.newInstance();
        } catch (Exception e) {
            throw new CubeException("Cube实例化失败: " + cubeDefinition.getClassName(), e);
        }
    }
}
