package io.github.tml.mosaic.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * JAR包信息实体
 */
@Data
public class JarPackageInfo {
    
    /**
     * 文件名
     */
    private String filename;
    
    /**
     * 文件大小(字节)
     */
    private Long size;
    
    /**
     * 文件大小(KB)
     */
    private Long sizeKB;
    
    /**
     * 最后修改时间
     */
    private LocalDateTime lastModified;
    
    /**
     * 完整路径
     */
    private String fullPath;
}