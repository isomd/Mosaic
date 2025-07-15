package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.domain.cube.CubeDomain;
import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.service.CubeConfigService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Cube信息查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CubeConfigServiceImpl implements CubeConfigService {

    private final CubeDomain cubeDomain;

    @Override
    public R<?> getCubeConfiguration(String cubeId) {
        log.debug("Service: Fetching configuration for cube ID: {}", cubeId);

        try {
            Map<String, Object> config = cubeDomain.getCubeConfiguration(cubeId);
            return R.success(config);
        } catch (Exception e) {
            log.error("Service: Failed to fetch configuration for cube ID: {}", cubeId, e);
            return R.error("获取Cube配置失败: " + e.getMessage());
        }
    }

    @Override
    public R<?> updateCubeConfiguration(CubeConfigUpdateReq configReq) {
        String cubeId = configReq.getCubeId();
        log.debug("Service: Updating configuration for cube ID: {} with request: {}", cubeId, configReq);
        try {
            Map<String, Object> updatedConfig = cubeDomain.updateCubeConfiguration(cubeId, configReq);
            return R.success(updatedConfig);
        } catch (IllegalArgumentException e) {
            log.warn("Service: Invalid configuration update request for cube ID: {}", cubeId, e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("Service: Failed to update configuration for cube ID: {}", cubeId, e);
            return R.error(e.getMessage());
        }
    }
}