package io.github.tml.mosaic.entity.vo;

import lombok.Data;

/**
 * JAR包操作结果
 */
@Data
public class JarOperationResult {
    
    /**
     * JAR包ID
     */
    private String jarId;
    
    /**
     * 操作是否成功
     */
    private boolean success;
    
    /**
     * 错误信息
     */
    private String errorMessage;
}