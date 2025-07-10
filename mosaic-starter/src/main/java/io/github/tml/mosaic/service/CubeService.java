package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
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
     * 获取指定Cube的配置信息
     * @param cubeId Cube标识
     * @return 配置信息响应
     */
    R<?> getCubeConfiguration(String cubeId);

    /**
     * 更新指定Cube的配置信息
     * @param configReq 配置更新请求
     * @return 更新结果响应
     */
    R<?> updateCubeConfiguration(CubeConfigUpdateReq configReq);

    /**
     * 更新Angel Cube的状态（启动/停止）
     * @param statusReq 状态更新请求
     * @return 更新结果响应
     */
    R<?> updateAngelCubeStatus(AngelCubeStatusUpdateReq statusReq);
}