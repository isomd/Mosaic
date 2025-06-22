package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.util.R;

/**
 * Cube服务接口
 */
public interface CubeService {

    R<?> getCubeList();

    R<?> getCubesByFilter(CubeFilterReq cubeFilterReq);

    R<?> getCubeById(String cubeId);

    R<?> getCubeOverview();

    R<?> checkCubeExists(String cubeId);
}