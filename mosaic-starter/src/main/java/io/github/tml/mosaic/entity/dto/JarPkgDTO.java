package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.entity.enums.JarInstallStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * JAR包信息DTO
 */
@Data
public class JarPkgDTO {

    /**
     * JAR包唯一标识(8位简短UUID)
     */
    private String id;

    /**
     * 文件名(用户可见的原始文件名，支持重命名)
     */
    private String filename;

    /**
     * 物理存储文件名(filename_简短UUID.jar)
     */
    private String storageFilename;

    /**
     * 文件大小(字节)
     */
    private Long size;

    /**
     * 文件大小(KB)
     */
    private Long sizeKB;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime lastModified;

    /**
     * 安装状态
     */
    private JarInstallStatus installStatus;

    /**
     * 安装时间
     */
    private LocalDateTime installTime;

    /**
     * 错误信息(安装失败时)
     */
    private String errorMessage;

    /**
     * 完整路径
     */
    private String fullPath;
}