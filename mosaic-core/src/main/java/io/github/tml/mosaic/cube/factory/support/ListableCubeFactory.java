package io.github.tml.mosaic.cube.factory.support;

import io.github.tml.mosaic.cube.factory.config.CubeDefinitionRegistry;

/**
 * 链式存储CubeFactory
 */
public abstract class ListableCubeFactory extends AbstractAutowireInitCubeFactory
        implements CubeDefinitionRegistry, ConfigurableCubeFactory {

}
