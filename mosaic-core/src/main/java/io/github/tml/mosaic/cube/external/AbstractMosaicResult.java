package io.github.tml.mosaic.cube.external;

public abstract class AbstractMosaicResult {
    public abstract  <T> T getResult(String key, Class<T> clazz);
}
