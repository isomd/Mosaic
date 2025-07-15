package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
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

    /**
     * 更新Angel Cube的状态（启动/停止）
     * @param statusReq 状态更新请求
     * @return 更新结果响应
     */
    R<?> updateAngelCubeStatus(AngelCubeStatusUpdateReq statusReq);
}