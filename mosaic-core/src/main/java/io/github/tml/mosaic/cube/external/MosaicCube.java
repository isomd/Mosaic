package io.github.tml.mosaic.cube.external;

import io.github.tml.mosaic.cube.api.CubeApi;

public abstract class MosaicCube implements CubeApi {

    public MosaicCube() {
    }

    @Override
    public boolean destroy() {
        return true;
    }
}
