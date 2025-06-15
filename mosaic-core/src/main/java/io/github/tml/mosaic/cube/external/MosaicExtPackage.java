package io.github.tml.mosaic.cube.external;

import io.github.tml.mosaic.cube.api.ExtensionPackageApi;

public abstract class MosaicExtPackage<T extends MosaicCube> implements ExtensionPackageApi {

    protected T mosaicCube;

    @Override
    public void initCube(MosaicCube mosaicCube) {
        this.mosaicCube = (T) mosaicCube;
    }
}
