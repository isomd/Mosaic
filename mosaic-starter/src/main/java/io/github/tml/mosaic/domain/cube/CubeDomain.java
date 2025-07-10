package io.github.tml.mosaic.domain.cube;

import io.github.tml.mosaic.convert.CubeConvert;
import io.github.tml.mosaic.converter.CubeDefinitionConverter;
import io.github.tml.mosaic.converter.InfoContextConverter;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.constant.CubeModelType;
import io.github.tml.mosaic.cube.external.AngelCube;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.entity.dto.CubeOverviewDTO;
import io.github.tml.mosaic.entity.vo.cube.CubeInfoVO;
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
import java.util.concurrent.ConcurrentHashMap;
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

    // 存储Angel Cube的运行状态，默认为INACTIVE
    private final Map<String, CubeInfoVO.CubeStatus> angelCubeStatusMap = new ConcurrentHashMap<>();

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
     * 获取Cube配置信息
     * @param cubeId Cube标识
     */
    public Map<String, Object> getCubeConfiguration(String cubeId) {
        log.debug("Domain: Fetching configuration for cube ID: {}", cubeId);

        try {
            // 从上下文获取配置
            Map<String, Object> configurations = cubeContext.getCubeConfiguration(cubeId);

            log.info("Domain: Successfully retrieved configuration for cube ID: {}", cubeId);
            return configurations;
        } catch (Exception e) {
            log.error("Domain: Failed to fetch configuration for cube ID: {}", cubeId, e);
            throw new RuntimeException("获取Cube配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新Angel Cube状态
     * @param cubeId Cube标识
     * @param action 执行动作
     * @return 操作结果
     */
    public Map<String, Object> updateAngelCubeStatus(String cubeId, AngelCubeStatusUpdateReq.AngelCubeAction action) {
        log.debug("Domain: Updating Angel Cube status for ID: {} with action: {}", cubeId, action);

        try {
            // 1. 验证Cube是否存在且为function类型
            validateAngelCube(cubeId);

            // 2. 获取Cube实例
            GUID guid = new GUUID(cubeId);
            io.github.tml.mosaic.cube.Cube cube = cubeContext.getCube(guid);

            // 3. 转换为AngelCube并执行相应操作
            AngelCube angelCube = convertToAngelCube(cube);
            CubeInfoVO.CubeStatus newStatus = executeAngelCubeAction(angelCube, action);

            // 4. 更新状态缓存
            angelCubeStatusMap.put(cubeId, newStatus);

            Map<String, Object> result = Map.of(
                    "cubeId", cubeId,
                    "action", action.name(),
                    "actionDescription", action.getDescription(),
                    "status", newStatus.name(),
                    "statusDescription", newStatus.getDescription(),
                    "timestamp", java.time.LocalDateTime.now()
            );

            log.info("Domain: Successfully updated Angel Cube status for ID: {} to {}", cubeId, newStatus);
            return result;

        } catch (Exception e) {
            log.error("Domain: Failed to update Angel Cube status for ID: {}", cubeId, e);
            throw new RuntimeException("更新Angel Cube状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证是否为有效的Angel Cube
     */
    private void validateAngelCube(String cubeId) {
        // 检查Cube是否存在
        Optional<CubeDTO> cubeOpt = getCubeById(cubeId);
        if (cubeOpt.isEmpty()) {
            throw new IllegalArgumentException("Cube不存在，ID: " + cubeId);
        }

        CubeDTO cube = cubeOpt.get();

        // 检查是否为angle类型
        if (!CubeModelType.ANGLE_TYPE.equals(cube.getModel())) {
            throw new IllegalArgumentException("只有angle类型的Cube才支持状态管理，当前类型: " + cube.getModel());
        }

        log.debug("Angel Cube validation passed for ID: {}", cubeId);
    }

    /**
     * 将Cube转换为AngelCube
     */
    private AngelCube convertToAngelCube(Cube cube) {
        Object mosaicCube = cube.getMosaicCube();

        if (!(mosaicCube instanceof AngelCube)) {
            throw new IllegalArgumentException("该Cube不是AngelCube类型，无法执行启动/停止操作");
        }

        return (AngelCube) mosaicCube;
    }

    /**
     * 执行Angel Cube动作
     */
    private CubeInfoVO.CubeStatus executeAngelCubeAction(AngelCube angelCube, AngelCubeStatusUpdateReq.AngelCubeAction action) {
        try {
            switch (action) {
                case START:
                    angelCube.start();
                    log.info("Angel Cube started successfully");
                    return CubeInfoVO.CubeStatus.ACTIVE;

                case STOP:
                    angelCube.stop();
                    log.info("Angel Cube stopped successfully");
                    return CubeInfoVO.CubeStatus.INACTIVE;

                default:
                    throw new IllegalArgumentException("不支持的操作: " + action);
            }
        } catch (Exception e) {
            log.error("Angel Cube action execution failed: {}", action, e);
            throw new RuntimeException("Angel Cube操作执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新Cube配置信息
     * @param cubeId Cube标识
     * @param configReq 配置更新请求
     */
    public Map<String, Object> updateCubeConfiguration(String cubeId, CubeConfigUpdateReq configReq) {
        log.debug("Domain: Updating configuration for cube ID: {} with request: {}", cubeId, configReq);

        try {
            // 执行配置更新
            Map<String, Object> updatedConfigurations = cubeContext.updateConfigurations(
                    cubeId,
                    configReq.getConfigurations()
            );

            log.info("Domain: Successfully updated configuration for cube ID: {}", cubeId);
            return updatedConfigurations;
        } catch (Exception e) {
            log.error("Domain: Failed to update configuration for cube ID: {}", cubeId, e);
            throw new RuntimeException("更新Cube配置失败: " + e.getMessage(), e);
        }
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