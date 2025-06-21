package io.github.tml.mosaic.entity.vo;

import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import lombok.Data;

import java.util.List;

/**
 * JAR包安装结果
 */
@Data
public class JarInstallResult {
    
    /**
     * JAR包ID
     */
    private String jarId;
    
    /**
     * 安装是否成功
     */
    private boolean installSuccess;
    
    /**
     * Cube注册结果列表
     */
    private List<CubeRegistrationResult> registrationResults;
    
    /**
     * 错误信息
     */
    private String errorMessage;
}
