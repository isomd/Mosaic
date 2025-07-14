package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.convert.CubeConvert;
import io.github.tml.mosaic.domain.cube.CubeDomain;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.entity.vo.cube.CubeInfoVO;
import io.github.tml.mosaic.service.CubeService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Cube信息查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CubeInfoService implements CubeService {

    private final CubeDomain cubeDomain;

    @Override
    public R<?> getCubeList() {
        List<CubeDTO> cubeList = cubeDomain.getCubeList();
        List<CubeInfoVO> cubeVOs = CubeConvert.convert2VOs(cubeList);
        
        return R.success(Map.of(
                "cubeList", cubeVOs,
                "total", cubeVOs.size()
        ));
    }

    @Override
    public R<?> getCubesByFilter(CubeFilterReq cubeFilterReq) {
        log.debug("Service: Applying filter criteria: {}", cubeFilterReq);
        
        List<CubeDTO> cubeList = cubeDomain.getCubesByFilter(cubeFilterReq);
        List<CubeInfoVO> cubeVOs = CubeConvert.convert2VOs(cubeList);
        
        return R.success(Map.of(
                "cubeList", cubeVOs,
                "total", cubeVOs.size(),
                "searchCriteria", Map.of(
                        "name", cubeFilterReq.getName() != null ? cubeFilterReq.getName() : "",
                        "model", cubeFilterReq.getModel() != null ? cubeFilterReq.getModel() : "",
                        "version", cubeFilterReq.getVersion() != null ? cubeFilterReq.getVersion() : ""
                )
        ));
    }

    @Override
    public R<?> getCubeById(String cubeId) {
        log.debug("Service: Fetching cube for ID: {}", cubeId);
        
        return cubeDomain.getCubeById(cubeId)
                .map(CubeConvert::convert2VO)
                .map(R::success)
                .orElse(R.error("Cube not found for ID: " + cubeId));
    }

    @Override
    public R<?> getCubeOverview() {
        log.debug("Service: Generating cube overview");
        return R.success(cubeDomain.generateCubeOverview());
    }

    @Override
    public R<?> checkCubeExists(String cubeId) {
        boolean exists = cubeDomain.hasCube(cubeId);
        
        return R.success(Map.of(
                "cubeId", cubeId,
                "exists", exists
        ));
    }

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

    @Override
    public R<?> updateAngelCubeStatus(AngelCubeStatusUpdateReq statusReq) {
        String cubeId = statusReq.getCubeId();
        AngelCubeStatusUpdateReq.AngelCubeAction action = statusReq.getAction();

        log.debug("Service: Updating Angel Cube status for ID: {} with action: {}", cubeId, action);

        try {

            Map<String, Object> result = Map.of(
                    "cubeId", cubeId,
                    "timestamp", java.time.LocalDateTime.now()
            );

            return R.success(result);
        } catch (IllegalArgumentException e) {
            log.warn("Service: Invalid Angel Cube status update request for ID: {}", cubeId, e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("Service: Failed to update Angel Cube status for ID: {}", cubeId, e);
            return R.error("update angel cube running status fail: " + e.getMessage());
        }
    }
}