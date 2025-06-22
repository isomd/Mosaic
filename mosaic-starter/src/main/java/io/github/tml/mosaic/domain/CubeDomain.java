package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.convert.CubeConvert;
import io.github.tml.mosaic.converter.CubeDefinitionConverter;
import io.github.tml.mosaic.converter.InfoContextConverter;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.entity.dto.CubeOverviewDTO;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.install.support.ReaderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Cube领域服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CubeDomain {

    private final CubeContext cubeContext;
    private final InfoContextInstaller installer;

    /**
     * 获取所有Cube列表
     */
    public List<CubeDTO> getCubeList() {
        log.debug("Domain: Fetching all cube definitions");
        
        List<CubeDefinition> cubeDefinitions = cubeContext.getAllCubeDefinitions();
        List<CubeDTO> result = cubeDefinitions.stream()
                .map(CubeConvert::convert2DTO)
                .sorted(Comparator.comparing(CubeDTO::getId))
                .collect(Collectors.toList());
        
        log.info("Domain: Successfully retrieved {} cube definitions", result.size());
        return result;
    }

    /**
     * 安装并注册Cube
     * @param configLocations 配置位置数组
     * @return 注册结果列表
     */
    public List<CubeRegistrationResult> installAndRegisterCubes(String... configLocations) {
        try {
            // 转换信息上下文
            List<CubeInfo> cubeInfos = InfoContextConverter.convertInfoContextsToCubeInfoList(
                    installer.installCubeInfoContext(configLocations)
            );

            // 转换为Cube定义
            List<CubeDefinition> cubeDefinitions = CubeDefinitionConverter.convertCubeInfoListToCubeDefinitionList(cubeInfos);

            // 注册到上下文容器
            return cubeContext.registerAllCubeDefinition(cubeDefinitions);

        } catch (Exception e) {
            log.error("安装并注册Cube失败", e);
            throw new RuntimeException("安装并注册Cube失败: " + e.getMessage(), e);
        }
    }

    /**
     * 通过JAR路径安装并注册Cube
     * @param jarPath JAR文件路径
     * @return 注册结果列表
     */
    public List<CubeRegistrationResult> installAndRegisterCube(Path jarPath) {
        String[] configLocations = new String[]{ReaderType.FILE.getPrefix() + jarPath};
        return installAndRegisterCubes(configLocations);
    }

    /**
     * 根据过滤条件获取Cube信息
     */
    public List<CubeDTO> getCubesByFilter(CubeFilterReq filterDTO) {
        log.debug("Domain: Applying filter criteria: {}", filterDTO);
        
        List<CubeDefinition> allDefinitions = cubeContext.getAllCubeDefinitions();
        
        List<CubeDefinition> filteredDefinitions = allDefinitions.stream()
                .filter(buildDomainFilterPredicate(filterDTO))
                .collect(Collectors.toList());
        
        List<CubeDTO> result = filteredDefinitions.stream()
                .map(CubeConvert::convert2DTO)
                .sorted(Comparator.comparing(CubeDTO::getId))
                .collect(Collectors.toList());
        
        log.info("Domain: Filter applied, found {} matching cubes", result.size());
        return result;
    }

    /**
     * 根据ID获取特定Cube信息
     */
    public Optional<CubeDTO> getCubeById(String cubeId) {
        log.debug("Domain: Fetching cube definition for ID: {}", cubeId);
        
        Map<GUID, CubeDefinition> definitionMap = cubeContext.getAllCubeDefinitionMap();
        CubeDefinition definition = definitionMap.get(new GUUID(cubeId));
        
        if (definition == null) {
            log.warn("Domain: Cube definition not found for ID: {}", cubeId);
            return Optional.empty();
        }
        
        CubeDTO result = CubeConvert.convert2DTO(definition);
        log.debug("Domain: Successfully retrieved cube definition for ID: {}", cubeId);
        
        return Optional.of(result);
    }

    /**
     * 生成Cube概览统计信息
     */
    public CubeOverviewDTO generateCubeOverview() {
        log.debug("Domain: Generating cube overview statistics");
        
        List<CubeDefinition> allDefinitions = cubeContext.getAllCubeDefinitions();
        CubeOverviewDTO overview = CubeConvert.convertToOverviewDTO(allDefinitions);
        
        log.info("Domain: Successfully generated cube overview statistics");
        return overview;
    }

    /**
     * 检查Cube是否存在
     */
    public boolean hasCube(String cubeId) {
        log.debug("Domain: Checking cube existence for ID: {}", cubeId);
        boolean exists = cubeContext.containsCubeDefinition(cubeId);
        log.debug("Domain: Cube {} existence check result: {}", cubeId, exists);
        return exists;
    }

    /**
     * 获取Cube的扩展包数量
     */
    public int getExtensionPackageCount(String cubeId) {
        return getCubeById(cubeId)
                .map(cube -> cube.getExtensionPackages().size())
                .orElse(0);
    }

    /**
     * 获取Cube的扩展点总数
     */
    public int getTotalExtensionPointCount(String cubeId) {
        return getCubeById(cubeId)
                .map(cube -> cube.getExtensionPackages().stream()
                        .mapToInt(pkg -> pkg.getExtensionPoints().size())
                        .sum())
                .orElse(0);
    }

    /**
     * 构建领域级别的过滤谓词
     */
    private Predicate<CubeDefinition> buildDomainFilterPredicate(CubeFilterReq filterDTO) {
        Predicate<CubeDefinition> predicate = def -> true;

        if (filterDTO.hasName()) {
            predicate = predicate.and(def -> 
                def.getName().toLowerCase().contains(filterDTO.getName().toLowerCase()));
        }

        if (filterDTO.hasModel()) {
            predicate = predicate.and(def -> 
                filterDTO.getModel().equals(def.getModel()));
        }

        if (filterDTO.hasVersion()) {
            predicate = predicate.and(def -> 
                filterDTO.getVersion().equals(def.getVersion()));
        }

        return predicate;
    }
}