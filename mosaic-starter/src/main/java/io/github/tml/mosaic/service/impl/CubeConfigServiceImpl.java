package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.domain.cube.CubeDomain;
import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeMultiConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigCloneReq;
import io.github.tml.mosaic.service.CubeConfigService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Cube配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CubeConfigServiceImpl implements CubeConfigService {

    private final CubeDomain cubeDomain;

    @Override
    public R<?> getCubeConfiguration(String cubeId) {
        log.debug("Service: Fetching default configuration for cube ID: {}", cubeId);

        try {
            Map<String, Object> config = cubeDomain.getCubeConfiguration(cubeId);
            return R.success(config);
        } catch (Exception e) {
            log.error("Service: Failed to fetch default configuration for cube ID: {}", cubeId, e);
            return R.error("获取Cube默认配置失败: " + e.getMessage());
        }
    }

    @Override
    public R<?> getAllCubeConfigurations(String cubeId) {
        log.debug("Service: Fetching all configurations for cube ID: {}", cubeId);

        try {
            Map<String, Map<String, Object>> configs = cubeDomain.getAllCubeConfigurations(cubeId);
            return R.success(configs);
        } catch (Exception e) {
            log.error("Service: Failed to fetch all configurations for cube ID: {}", cubeId, e);
            return R.error("获取Cube所有配置失败: " + e.getMessage());
        }
    }

    @Override
    public R<?> getCubeConfiguration(String cubeId, String configId) {
        log.debug("Service: Fetching configuration for cube ID: {}, config ID: {}", cubeId, configId);

        try {
            Map<String, Object> config = cubeDomain.getCubeConfiguration(cubeId, configId);
            return R.success(config);
        } catch (Exception e) {
            log.error("Service: Failed to fetch configuration for cube ID: {}, config ID: {}", cubeId, configId, e);
            return R.error("获取Cube指定配置失败: " + e.getMessage());
        }
    }

    @Override
    public R<?> updateCubeConfiguration(CubeConfigUpdateReq configReq) {
        String cubeId = configReq.getCubeId();
        log.debug("Service: Updating default configuration for cube ID: {} with request: {}", cubeId, configReq);

        try {
            Map<String, Object> updatedConfig = cubeDomain.updateCubeConfiguration(cubeId, configReq);
            return R.success(updatedConfig);
        } catch (IllegalArgumentException e) {
            log.warn("Service: Invalid default configuration update request for cube ID: {}", cubeId, e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("Service: Failed to update default configuration for cube ID: {}", cubeId, e);
            return R.error(e.getMessage());
        }
    }

    @Override
    public R<?> updateCubeConfiguration(CubeMultiConfigUpdateReq configReq) {
        String cubeId = configReq.getCubeId();
        String configId = configReq.getConfigId();
        log.debug("Service: Updating configuration for cube ID: {}, config ID: {} with request: {}",
                cubeId, configId, configReq);

        try {
            Map<String, Object> updatedConfig = cubeDomain.updateCubeConfiguration(configReq);
            return R.success(updatedConfig);
        } catch (IllegalArgumentException e) {
            log.warn("Service: Invalid configuration update request for cube ID: {}, config ID: {}", cubeId, configId, e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("Service: Failed to update configuration for cube ID: {}, config ID: {}", cubeId, configId, e);
            return R.error(e.getMessage());
        }
    }

    @Override
    public R<?> removeCubeConfiguration(String cubeId, String configId) {
        log.debug("Service: Removing configuration for cube ID: {}, config ID: {}", cubeId, configId);

        try {
            boolean removed = cubeDomain.removeCubeConfiguration(cubeId, configId);
            return R.success(removed);
        } catch (IllegalArgumentException e) {
            log.warn("Service: Invalid configuration remove request for cube ID: {}, config ID: {}", cubeId, configId, e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("Service: Failed to remove configuration for cube ID: {}, config ID: {}", cubeId, configId, e);
            return R.error(e.getMessage());
        }
    }

    @Override
    public R<?> cloneCubeConfiguration(CubeConfigCloneReq cloneReq) {
        String cubeId = cloneReq.getCubeId();
        String sourceConfigId = cloneReq.getSourceConfigId();
        log.debug("Service: Cloning configuration for cube ID: {}, source config ID: {} with request: {}",
                cubeId, sourceConfigId, cloneReq);

        try {
            String newConfigId = cubeDomain.cloneCubeConfiguration(cloneReq);
            return R.success(newConfigId);
        } catch (IllegalArgumentException e) {
            log.warn("Service: Invalid configuration clone request for cube ID: {}, source config ID: {}", cubeId, sourceConfigId, e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("Service: Failed to clone configuration for cube ID: {}, source config ID: {}", cubeId, sourceConfigId, e);
            return R.error(e.getMessage());
        }
    }
}