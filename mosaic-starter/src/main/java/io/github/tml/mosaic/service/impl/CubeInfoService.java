package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.convert.CubeConvert;
import io.github.tml.mosaic.domain.CubeDomain;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.vo.CubeInfoVO;
import io.github.tml.mosaic.entity.vo.CubeOverviewVO;
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
    public R<?> getCubesByFilter(CubeFilterCriteria criteria) {
        log.debug("Service: Applying filter criteria: {}", criteria);
        
        List<CubeDTO> cubeList = cubeDomain.getCubesByFilter(
                CubeConvert.convertToDomainFilter(criteria)
        );
        List<CubeInfoVO> cubeVOs = CubeConvert.convert2VOs(cubeList);
        
        return R.success(Map.of(
                "cubeList", cubeVOs,
                "total", cubeVOs.size(),
                "searchCriteria", Map.of(
                        "name", criteria.getName() != null ? criteria.getName() : "",
                        "model", criteria.getModel() != null ? criteria.getModel() : "",
                        "version", criteria.getVersion() != null ? criteria.getVersion() : ""
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
        
        CubeOverviewVO overview = CubeConvert.convertOverviewToVO(
                cubeDomain.generateCubeOverview()
        );
        
        return R.success(overview);
    }

    @Override
    public R<?> checkCubeExists(String cubeId) {
        boolean exists = cubeDomain.hasCube(cubeId);
        
        return R.success(Map.of(
                "cubeId", cubeId,
                "exists", exists
        ));
    }

    /**
     * 过滤条件 - 保持向后兼容
     */
    public static class CubeFilterCriteria {
        private String name;
        private String model;
        private String version;

        // getter/setter methods
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public boolean hasName() {
            return name != null && !name.trim().isEmpty();
        }
        public boolean hasModel() {
            return model != null && !model.trim().isEmpty();
        }
        public boolean hasVersion() {
            return version != null && !version.trim().isEmpty();
        }

        @Override
        public String toString() {
            return String.format("CubeFilterCriteria{name='%s', model='%s', version='%s'}", name, model, version);
        }
    }
}