package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.dto.JarPkgDTO;
import io.github.tml.mosaic.entity.vo.JarInstallResult;
import io.github.tml.mosaic.entity.vo.JarOperationResult;
import io.github.tml.mosaic.entity.vo.JarUploadResult;
import io.github.tml.mosaic.util.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * JAR包管理服务接口
 */
public interface JarPkgService {

    // ==================== 文件管理操作 ====================

    /**
     * 批量上传JAR包
     */
    R<List<JarUploadResult>> batchUploadJarPackages(MultipartFile[] files);

    /**
     * 获取JAR包列表
     */
    R<List<JarPkgDTO>> listJarPackages();

    /**
     * 重命名JAR包(通过ID)
     */
    R<JarOperationResult> renameJarPackage(String jarId, String newFilename);

    /**
     * 批量删除JAR包(通过ID) - 包括文件和状态记录
     */
    R<List<JarOperationResult>> batchDeleteJarPackages(List<String> jarIds);

    // ==================== Cube管理操作 ====================

    /**
     * 批量安装JAR包(注册Cube定义)
     */
    R<List<JarInstallResult>> batchInstallJarPackages(List<String> jarIds);
}