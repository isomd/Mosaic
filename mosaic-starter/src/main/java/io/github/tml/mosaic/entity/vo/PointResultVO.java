package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.cube.factory.definition.ExtensionPointDefinition;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * 描述: 扩展点结果定义前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResultVO {

    private String resultType;
    private String description;
    private Boolean required;

    /**
     * 从ExtensionPointDefinition转换为PointResultVO
     */
    public static PointResultVO fromDefinition(ExtensionPointDefinition definition) {
        if (definition == null || definition.getPointResultDefinitions() == null) {
            return buildDefaultResult(definition);
        }

        // 这里根据实际的PointResultDefinition结构来实现
        return PointResultVO.builder()
                .resultType(extractResultType(definition))
                .description("扩展点执行结果")
                .required(true)
                .build();
    }

    /**
     * 构建默认结果定义
     */
    private static PointResultVO buildDefaultResult(ExtensionPointDefinition definition) {
        if (definition == null) {
            return null;
        }

        return PointResultVO.builder()
                .resultType(extractResultType(definition))
                .description("默认扩展点结果")
                .required(false)
                .build();
    }

    /**
     * 提取结果类型
     */
    private static String extractResultType(ExtensionPointDefinition definition) {
        return Optional.ofNullable(definition.getReturnType())
                .map(Class::getSimpleName)
                .orElse("void");
    }
}