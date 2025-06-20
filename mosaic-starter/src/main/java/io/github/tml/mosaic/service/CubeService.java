package io.github.tml.mosaic.service;

import io.github.tml.mosaic.service.impl.CubeInfoService;
import io.github.tml.mosaic.util.R;

/**
 * Cube服务接口
 */
public interface CubeService {

    R<?> getCubeList();

    R<?> getCubesByFilter(CubeInfoService.CubeFilterCriteria criteria);

    R<?> getCubeById(String cubeId);

    R<?> getCubeOverview();

    R<?> checkCubeExists(String cubeId);
}