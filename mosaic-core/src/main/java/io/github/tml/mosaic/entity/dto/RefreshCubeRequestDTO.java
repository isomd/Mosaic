package io.github.tml.mosaic.entity.dto;

import lombok.Data;

/**
 * @author welsir
 * @description :
 * @date 2025/5/29
 */
@Data
public class RefreshCubeRequestDTO {

    /**
     * 类名
     */
    private String className;

    /**
     * class类路径
     */
    private String classPath;
}