package io.github.tml.mosaic.core.factory.support;


import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.CubeFactory;

// TODO 有点歧义
public interface ConfigurableCubeFactory extends CubeFactory {

    void preInstantiateSingletons() throws CubeException;
}