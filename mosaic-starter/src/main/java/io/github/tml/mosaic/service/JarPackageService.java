package io.github.tml.mosaic.service;
import io.github.tml.mosaic.entity.JarPackageInfo;
import io.github.tml.mosaic.entity.vo.JarUploadResult;
import io.github.tml.mosaic.util.R;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * JAR包管理服务接口
 * @author suifeng
 * @date 2025/6/15
 */
public interface JarPackageService {

    R<JarUploadResult> uploadJarPackage(MultipartFile file);

    R<List<JarUploadResult>> batchUploadJarPackages(MultipartFile[] files);

    R<List<JarPackageInfo>> listJarPackages();

    R<Void> renameJarPackage(String oldFilename, String newFilename);

    R<Void> deleteJarPackage(String filename);
}