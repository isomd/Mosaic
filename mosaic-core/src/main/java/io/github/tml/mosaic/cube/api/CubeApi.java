package io.github.tml.mosaic.cube.api;

import io.github.tml.mosaic.cube.CubeConfig;
import io.github.tml.mosaic.cube.CubeConfigThreadLocal;

public interface CubeApi {

    boolean init();

    boolean destroy();

    String cubeId();

    default CubeConfig getCubeConfig() {
        return CubeConfigThreadLocal.get(cubeId());
    }
}
