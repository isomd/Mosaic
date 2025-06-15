package io.github.tml.mosaic.cube.api;

import io.github.tml.mosaic.cube.external.MosaicCube;

public interface ExtensionPackageApi{

    String extPackageId();

    void initCube(MosaicCube mosaicCube);
}
