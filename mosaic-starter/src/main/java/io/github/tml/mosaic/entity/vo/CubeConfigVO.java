package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 描述: Cube配置信息前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CubeConfigVO {

    private Boolean singleton;
    private Boolean lazyInit;
    private String initMethod;
    private String destroyMethod;
    private String[] dependencies;
    private Object properties;

    /**
     * 从CubeDefinition转换为CubeConfigVO
     */
    public static CubeConfigVO fromDefinition(CubeDefinition definition) {
        if (definition == null) {
            return buildDefaultConfig();
        }

        // 根据实际的CubeConfigInfo结构来实现
        if (definition.getCubeConfigInfo() != null) {
            return buildFromConfigInfo(definition);
        }

        return buildDefaultConfig();
    }

    /**
     * 从配置信息构建VO
     */
    private static CubeConfigVO buildFromConfigInfo(CubeDefinition definition) {
        // 这里需要根据实际的CubeConfigInfo结构来实现
        return CubeConfigVO.builder()
                .singleton(true) // 从definition.getCubeConfigInfo()获取
                .lazyInit(false)
                .initMethod("init")
                .destroyMethod("destroy")
                .dependencies(new String[0])
                .properties(null)
                .build();
    }

    /**
     * 构建默认配置
     */
    private static CubeConfigVO buildDefaultConfig() {
        return CubeConfigVO.builder()
                .singleton(true)
                .lazyInit(false)
                .initMethod(null)
                .destroyMethod(null)
                .dependencies(new String[0])
                .properties(null)
                .build();
    }
}