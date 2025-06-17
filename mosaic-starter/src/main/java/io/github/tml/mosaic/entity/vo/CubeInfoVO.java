package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述: Cube插件信息前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CubeInfoVO {

    private String id;
    private String name;
    private String version;
    private String description;
    private String model;
    private String className;
    private CubeStatus status;
    private List<ExtensionPackageVO> extensionPackages;
    private CubeConfigVO config;
    private CubeStatisticsVO statistics;
    private LocalDateTime registeredTime;
    private LocalDateTime lastUpdatedTime;

    /**
     * 从CubeDefinition转换为CubeInfoVO
     */
    public static CubeInfoVO fromDefinition(CubeDefinition definition) {
        if (definition == null) {
            return null;
        }

        List<ExtensionPackageVO> extensionPackageVOs = definition.getExtensionPackages()
                .stream()
                .map(ExtensionPackageVO::fromDefinition)
                .collect(Collectors.toList());

        CubeStatisticsVO statistics = CubeStatisticsVO.fromDefinition(definition);
        CubeConfigVO config = CubeConfigVO.fromDefinition(definition);

        return CubeInfoVO.builder()
                .id(definition.getId())
                .name(definition.getName())
                .version(definition.getVersion())
                .description(definition.getDescription())
                .model(definition.getModel())
                .className(definition.getClassName())
                .status(determineStatus(definition))
                .extensionPackages(extensionPackageVOs)
                .config(config)
                .statistics(statistics)
                .registeredTime(LocalDateTime.now()) // 实际应从定义中获取
                .lastUpdatedTime(LocalDateTime.now()) // 实际应从定义中获取
                .build();
    }

    /**
     * 批量转换CubeDefinition列表
     */
    public static List<CubeInfoVO> fromDefinitions(List<CubeDefinition> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return List.of();
        }
        
        return definitions.stream()
                .map(CubeInfoVO::fromDefinition)
                .collect(Collectors.toList());
    }

    /**
     * 确定Cube状态
     */
    private static CubeStatus determineStatus(CubeDefinition definition) {
        if (definition.getClassLoader() != null) {
            return CubeStatus.ACTIVE;
        }
        return CubeStatus.INACTIVE;
    }

    public enum CubeStatus {
        ACTIVE("运行中"),
        INACTIVE("未激活"),
        ERROR("错误"),
        LOADING("加载中");

        private final String description;

        CubeStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}