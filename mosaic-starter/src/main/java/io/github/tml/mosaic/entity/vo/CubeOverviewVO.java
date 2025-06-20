package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述: Cube概览统计信息
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CubeOverviewVO {

    private Integer totalCubes;
    private Integer totalExtensionPackages;
    private Integer totalExtensionPoints;
    private Map<String, Long> cubesByModel;

    /**
     * 从CubeDefinition列表生成概览统计
     */
    public static CubeOverviewVO fromDefinitions(List<CubeDefinition> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return buildEmptyOverview();
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

        return CubeOverviewVO.builder()
                .totalCubes(totalCubes)
                .totalExtensionPackages(totalExtensionPackages)
                .totalExtensionPoints(totalExtensionPoints)
                .cubesByModel(cubesByModel)
                .build();
    }

    /**
     * 构建空概览信息
     */
    public static CubeOverviewVO buildEmptyOverview() {
        return CubeOverviewVO.builder()
                .totalCubes(0)
                .totalExtensionPackages(0)
                .totalExtensionPoints(0)
                .cubesByModel(Map.of())
                .build();
    }
}