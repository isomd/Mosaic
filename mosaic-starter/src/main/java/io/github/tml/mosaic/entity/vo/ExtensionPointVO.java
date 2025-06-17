package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.cube.factory.definition.ExtensionPointDefinition;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * 描述: 扩展点前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionPointVO {

    private String id;
    private String methodName;
    private String extensionName;
    private Integer priority;
    private String description;
    private Boolean asyncFlag;
    private String returnType;
    private String[] parameterTypes;
    private PointResultVO pointResult;

    /**
     * 从ExtensionPointDefinition转换为ExtensionPointVO
     */
    public static ExtensionPointVO fromDefinition(ExtensionPointDefinition definition) {
        if (definition == null) {
            return null;
        }

        String[] parameterTypes = extractParameterTypes(definition);
        String returnType = extractReturnType(definition);
        PointResultVO pointResult = PointResultVO.fromDefinition(definition);

        return ExtensionPointVO.builder()
                .id(definition.getId())
                .methodName(definition.getMethodName())
                .extensionName(definition.getExtensionName())
                .priority(definition.getPriority())
                .description(definition.getDescription())
                .asyncFlag(definition.isAsyncFlag())
                .returnType(returnType)
                .parameterTypes(parameterTypes)
                .pointResult(pointResult)
                .build();
    }

    /**
     * 提取参数类型信息
     */
    private static String[] extractParameterTypes(ExtensionPointDefinition definition) {
        return Optional.ofNullable(definition.getParameterTypes())
                .map(types -> Arrays.stream(types)
                        .map(Class::getSimpleName)
                        .toArray(String[]::new))
                .orElse(new String[0]);
    }

    /**
     * 提取返回类型信息
     */
    private static String extractReturnType(ExtensionPointDefinition definition) {
        return Optional.ofNullable(definition.getReturnType())
                .map(Class::getSimpleName)
                .orElse("void");
    }
}