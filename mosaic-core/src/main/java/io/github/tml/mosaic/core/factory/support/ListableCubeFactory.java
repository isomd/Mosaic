package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.factory.config.CubeDefinitionRegistry;

/**
 * 链式存储CubeFactory
 */
public abstract class ListableCubeFactory extends AbstractAutowireInitCubeFactory
        implements CubeDefinitionRegistry, ConfigurableCubeFactory {
}
