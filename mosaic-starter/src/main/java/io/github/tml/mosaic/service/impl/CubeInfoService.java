package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.entity.vo.CubeInfoVO;
import io.github.tml.mosaic.entity.vo.CubeOverviewVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 描述: Cube插件信息查询服务
 * @author suifeng
 * 日期: 2025/6/7
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CubeInfoService {

    private final CubeContext cubeContext;

    /**
     * 获取所有Cube插件信息
     */
    public List<CubeInfoVO> getAllCubeInfos() {
        log.debug("Fetching all cube definitions from context");
        
        List<CubeDefinition> cubeDefinitions = cubeContext.getAllCubeDefinitions();
        List<CubeInfoVO> result = CubeInfoVO.fromDefinitions(cubeDefinitions);
        
        log.info("Successfully retrieved {} cube definitions", result.size());
        return result;
    }

    /**
     * 根据条件过滤Cube插件信息
     */
    public List<CubeInfoVO> getCubeInfosByFilter(CubeFilterCriteria criteria) {
        log.debug("Fetching cube definitions with filter criteria: {}", criteria);
        
        List<CubeDefinition> allDefinitions = cubeContext.getAllCubeDefinitions();
        
        List<CubeDefinition> filteredDefinitions = allDefinitions.stream()
                .filter(buildFilterPredicate(criteria))
                .collect(Collectors.toList());
        
        List<CubeInfoVO> result = CubeInfoVO.fromDefinitions(filteredDefinitions);
        
        log.info("Found {} cube definitions matching filter criteria", result.size());
        return result;
    }

    /**
     * 获取Cube插件概览统计
     */
    public CubeOverviewVO getCubeOverview() {
        log.debug("Generating cube overview statistics");
        
        List<CubeDefinition> allDefinitions = cubeContext.getAllCubeDefinitions();
        CubeOverviewVO overview = CubeOverviewVO.fromDefinitions(allDefinitions);
        
        log.info("Successfully generated cube overview statistics");
        return overview;
    }

    /**
     * 根据ID获取特定Cube信息
     */
    public CubeInfoVO getCubeInfoById(String cubeId) {
        log.debug("Fetching cube definition for ID: {}", cubeId);
        
        Map<GUID, CubeDefinition> definitionMap = cubeContext.getAllCubeDefinitionMap();
        CubeDefinition definition = definitionMap.get(new GUUID(cubeId));
        
        if (definition == null) {
            log.warn("Cube definition not found for ID: {}", cubeId);
            return null;
        }
        
        CubeInfoVO result = CubeInfoVO.fromDefinition(definition);
        log.debug("Successfully retrieved cube definition for ID: {}", cubeId);
        
        return result;
    }

    /**
     * 检查Cube是否存在
     */
    public boolean existsCube(String cubeId) {
        return cubeContext.containsCubeDefinition(cubeId);
    }

    /**
     * 构建过滤谓词
     */
    private Predicate<CubeDefinition> buildFilterPredicate(CubeFilterCriteria criteria) {
        Predicate<CubeDefinition> predicate = def -> true;

        if (criteria.hasName()) {
            predicate = predicate.and(def -> 
                def.getName().toLowerCase().contains(criteria.getName().toLowerCase()));
        }

        if (criteria.hasModel()) {
            predicate = predicate.and(def -> 
                criteria.getModel().equals(def.getModel()));
        }

        if (criteria.hasVersion()) {
            predicate = predicate.and(def -> 
                criteria.getVersion().equals(def.getVersion()));
        }

        return predicate;
    }

    /**
     * Cube过滤条件
     */
    @Setter
    @Getter
    public static class CubeFilterCriteria {

        private String name;
        private String model;
        private String version;

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