package io.github.tml.mosaic.service;
import io.github.tml.mosaic.entity.JarPackageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * JAR包管理服务接口
 * @author suifeng
 * @date 2025/6/15
 */
public interface JarPackageService {
    
    /**
     * 上传JAR包
     * @param file 上传文件
     * @return 文件名
     * @throws IOException IO异常
     */
    String uploadJarPackage(MultipartFile file) throws IOException;
    
    /**
     * 获取JAR包列表
     * @return JAR包信息列表
     * @throws IOException IO异常
     */
    List<JarPackageInfo> listJarPackages() throws IOException;
    
    /**
     * 重命名JAR包
     * @param oldFilename 原文件名
     * @param newFilename 新文件名
     * @throws IOException IO异常
     */
    void renameJarPackage(String oldFilename, String newFilename) throws IOException;
    
    /**
     * 删除JAR包
     * @param filename 文件名
     * @throws IOException IO异常
     */
    void deleteJarPackage(String filename) throws IOException;
}