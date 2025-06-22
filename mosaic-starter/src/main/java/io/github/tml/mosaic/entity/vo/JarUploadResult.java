package io.github.tml.mosaic.entity.vo;

import lombok.Data;

/**
 * JAR包上传结果
 */
@Data
public class JarUploadResult {

    /**
     * JAR包ID
     */
    private String jarId;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 文件上传是否成功
     */
    private boolean uploadSuccess;

    /**
     * 错误信息
     */
    private String errorMessage;
}
