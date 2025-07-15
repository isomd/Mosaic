package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeMultiConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigCloneReq;
import io.github.tml.mosaic.util.R;

/**
 * Cube配置服务接口
 */
public interface CubeConfigService {

    /**
     * 获取指定Cube的默认配置信息
     * @param cubeId Cube标识
     * @return 配置信息响应
     */
    R<?> getCubeConfiguration(String cubeId);

    /**
     * 获取指定Cube的所有配置信息
     * @param cubeId Cube标识
     * @return 所有配置信息响应
     */
    R<?> getAllCubeConfigurations(String cubeId);

    /**
     * 获取指定Cube的特定配置信息
     * @param cubeId Cube标识
     * @param configId 配置标识
     * @return 配置信息响应
     */
    R<?> getCubeConfiguration(String cubeId, String configId);

    /**
     * 更新指定Cube的默认配置信息
     * @param configReq 配置更新请求
     * @return 更新结果响应
     */
    R<?> updateCubeConfiguration(CubeConfigUpdateReq configReq);

    /**
     * 更新指定Cube的特定配置信息
     * @param configReq 多配置更新请求
     * @return 更新结果响应
     */
    R<?> updateCubeConfiguration(CubeMultiConfigUpdateReq configReq);

    /**
     * 删除指定Cube的特定配置
     * @param cubeId Cube标识
     * @param configId 配置标识
     * @return 删除结果响应
     */
    R<?> removeCubeConfiguration(String cubeId, String configId);

    /**
     * 克隆指定Cube的配置
     * @param cloneReq 配置克隆请求
     * @return 新配置ID响应
     */
    R<?> cloneCubeConfiguration(CubeConfigCloneReq cloneReq);
}