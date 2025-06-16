package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.Cube;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述: 第三步：属性填充之后的cube初始化操作
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public abstract class AbstractAutowireInitCubeFactory extends AbstractAutowirePropertyCubeFactory {

    @Override
    protected Cube initializeCube(Cube cube, CubeDefinition cubeDefinition) {
        try {
            if (cube.init()) {
                log.info("✓ Cube init success | CubeId: {}, CubeName:{}", cube.getCubeId(), cube.getMetaData().getName());
                return cube;
            } else {
                throw new CubeException("Cube init failed, cubeId:" + cube.getCubeId());
            }
        } catch (Exception e) {
            throw new CubeException("Cube init failed, error:" + e.getMessage());
        }
    }
}
