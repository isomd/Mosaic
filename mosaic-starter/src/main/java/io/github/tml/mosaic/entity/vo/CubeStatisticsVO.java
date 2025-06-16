package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.factory.definition.ExtensionPointDefinition;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述: Cube统计信息前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CubeStatisticsVO {

    private Integer totalExtensionPackages;
    private Integer totalExtensionPoints;
    private Integer asyncExtensionPoints;
    private Integer syncExtensionPoints;
    private Double averagePriority;
    private Long memoryUsage;

    /**
     * 从CubeDefinition转换为CubeStatisticsVO
     */
    public static CubeStatisticsVO fromDefinition(CubeDefinition definition) {
        if (definition == null) {
            return buildEmptyStatistics();
        }

        List<ExtensionPointDefinition> allPoints = extractAllExtensionPoints(definition);
        
        int totalExtensionPackages = definition.getExtensionPackages().size();
        int totalExtensionPoints = allPoints.size();
        int asyncPoints = (int) allPoints.stream().filter(ExtensionPointDefinition::isAsyncFlag).count();
        int syncPoints = totalExtensionPoints - asyncPoints;
        
        double averagePriority = calculateAveragePriority(allPoints);
        long memoryUsage = estimateMemoryUsage(definition);

        return CubeStatisticsVO.builder()
                .totalExtensionPackages(totalExtensionPackages)
                .totalExtensionPoints(totalExtensionPoints)
                .asyncExtensionPoints(asyncPoints)
                .syncExtensionPoints(syncPoints)
                .averagePriority(averagePriority)
                .memoryUsage(memoryUsage)
                .build();
    }

    /**
     * 提取所有扩展点
     */
    private static List<ExtensionPointDefinition> extractAllExtensionPoints(CubeDefinition definition) {
        return definition.getExtensionPackages()
                .stream()
                .flatMap(pkg -> pkg.getExtensionPoints().stream())
                .collect(Collectors.toList());
    }

    /**
     * 计算平均优先级
     */
    private static double calculateAveragePriority(List<ExtensionPointDefinition> points) {
        if (points.isEmpty()) {
            return 0.0;
        }
        
        double average = points.stream()
                .mapToInt(ExtensionPointDefinition::getPriority)
                .average()
                .orElse(0.0);
        
        return Math.round(average * 100.0) / 100.0;
    }

    /**
     * 估算内存占用
     */
    private static long estimateMemoryUsage(CubeDefinition definition) {
        int baseSize = 1024; // 1KB基础大小
        int extensionPackageSize = definition.getExtensionPackages().size() * 512;
        int extensionPointSize = definition.getExtensionPackages()
                .stream()
                .mapToInt(pkg -> pkg.getExtensionPoints().size() * 256)
                .sum();
        
        return baseSize + extensionPackageSize + extensionPointSize;
    }

    /**
     * 构建空统计信息
     */
    private static CubeStatisticsVO buildEmptyStatistics() {
        return CubeStatisticsVO.builder()
                .totalExtensionPackages(0)
                .totalExtensionPoints(0)
                .asyncExtensionPoints(0)
                .syncExtensionPoints(0)
                .averagePriority(0.0)
                .memoryUsage(0L)
                .build();
    }
}