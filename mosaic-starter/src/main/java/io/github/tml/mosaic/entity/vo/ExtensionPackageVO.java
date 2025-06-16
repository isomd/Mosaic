package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.core.factory.definition.ExtensionPackageDefinition;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述: 扩展包前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionPackageVO {

    private String id;
    private String name;
    private String description;
    private String className;
    private String cubeId;
    private List<ExtensionPointVO> extensionPoints;
    private Integer extensionPointCount;

    /**
     * 从ExtensionPackageDefinition转换为ExtensionPackageVO
     */
    public static ExtensionPackageVO fromDefinition(ExtensionPackageDefinition definition) {
        if (definition == null) {
            return null;
        }

        List<ExtensionPointVO> extensionPointVOs = definition.getExtensionPoints()
                .stream()
                .map(ExtensionPointVO::fromDefinition)
                .collect(Collectors.toList());

        return ExtensionPackageVO.builder()
                .id(definition.getId())
                .name(definition.getName())
                .description(definition.getDescription())
                .className(definition.getClassName())
                .cubeId(definition.getCubeId())
                .extensionPoints(extensionPointVOs)
                .extensionPointCount(extensionPointVOs.size())
                .build();
    }

    /**
     * 批量转换ExtensionPackageDefinition列表
     */
    public static List<ExtensionPackageVO> fromDefinitions(List<ExtensionPackageDefinition> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return List.of();
        }
        
        return definitions.stream()
                .map(ExtensionPackageVO::fromDefinition)
                .collect(Collectors.toList());
    }
}