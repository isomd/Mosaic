package io.github.tml.mosaic.convert;

import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.dto.CubeOverviewDTO;
import io.github.tml.mosaic.entity.vo.cube.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cube转换器
 */
public class CubeConvert {

    /**
     * CubeDefinition -> CubeDTO (领域层转换)
     */
    public static CubeDTO convert2DTO(CubeDefinition definition) {
        if (definition == null) {
            return null;
        }

        CubeDTO cubeDTO = new CubeDTO(
                definition.getId(),
                definition.getName(),
                definition.getVersion(),
                definition.getDescription(),
                definition.getModel(),
                definition.getClassName(),
                definition.getClassLoader()
        );

        // 复制扩展包信息
        definition.getExtensionPackages().forEach(cubeDTO::addExtensionPackage);
        
        // 设置配置信息
        cubeDTO.setConfigInfo(definition.getConfigInfo());

        return cubeDTO;
    }

    /**
     * CubeDTO -> CubeInfoVO (应用层转换)
     */
    public static CubeInfoVO convert2VO(CubeDTO cubeDTO) {
        if (cubeDTO == null) {
            return null;
        }

        return CubeInfoVO.builder()
                .id(cubeDTO.getId())
                .name(cubeDTO.getName())
                .version(cubeDTO.getVersion())
                .description(cubeDTO.getDescription())
                .model(cubeDTO.getModel())
                .className(cubeDTO.getClassName())
                .status(convertStatus(cubeDTO.getStatus()))
                .extensionPackages(cubeDTO.getExtensionPackages().stream()
                        .map(ExtensionPackageVO::fromDefinition)
                        .collect(Collectors.toList()))
                .config(CubeConfigVO.fromDefinition(cubeDTO))
                .statistics(CubeStatisticsVO.fromDefinition(cubeDTO))
                .registeredTime(cubeDTO.getRegisteredTime())
                .lastUpdatedTime(cubeDTO.getLastUpdatedTime())
                .build();
    }

    /**
     * 批量转换 CubeDTO -> CubeInfoVO
     */
    public static List<CubeInfoVO> convert2VOs(List<CubeDTO> cubeDTOs) {
        if (cubeDTOs == null || cubeDTOs.isEmpty()) {
            return List.of();
        }
        
        return cubeDTOs.stream()
                .map(CubeConvert::convert2VO)
                .collect(Collectors.toList());
    }

    /**
     * 生成概览DTO
     */
    public static CubeOverviewDTO convertToOverviewDTO(List<CubeDefinition> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return buildEmptyOverviewDTO();
        }

        int totalCubes = definitions.size();
        
        int totalExtensionPackages = definitions.stream()
                .mapToInt(def -> def.getExtensionPackages().size())
                .sum();
        
        int totalExtensionPoints = definitions.stream()
                .flatMap(def -> def.getExtensionPackages().stream())
                .mapToInt(pkg -> pkg.getExtensionPoints().size())
                .sum();

        Map<String, Long> cubesByModel = definitions.stream()
                .collect(Collectors.groupingBy(
                    CubeDefinition::getModel,
                    Collectors.counting()
                ));

        return CubeOverviewDTO.builder()
                .totalCubes(totalCubes)
                .totalExtensionPackages(totalExtensionPackages)
                .totalExtensionPoints(totalExtensionPoints)
                .cubesByModel(cubesByModel)
                .build();
    }

    // ========== 私有辅助方法 ==========

    private static CubeInfoVO.CubeStatus convertStatus(CubeDTO.CubeStatus status) {
        return CubeInfoVO.CubeStatus.valueOf(status.name());
    }

    private static CubeOverviewDTO buildEmptyOverviewDTO() {
        return CubeOverviewDTO.builder()
                .totalCubes(0)
                .totalExtensionPackages(0)
                .totalExtensionPoints(0)
                .cubesByModel(Map.of())
                .build();
    }
}