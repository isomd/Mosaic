package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.config.properties.MosaicPluginProperties;
import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import io.github.tml.mosaic.domain.CubeDomain;
import io.github.tml.mosaic.entity.JarPackageInfo;
import io.github.tml.mosaic.entity.vo.JarUploadResult;
import io.github.tml.mosaic.service.JarPackageService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JarPackageServiceImpl implements JarPackageService {

    private final MosaicPluginProperties properties;
    private final CubeDomain cubeDomain;

    private static final String JAR_EXTENSION = ".jar";
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

    @PostConstruct
    public void initDirectory() {
        try {
            Path storageDir = resolveStoragePath();
            Files.createDirectories(storageDir);
            log.info("JAR包存储目录初始化完成: {}", storageDir.toAbsolutePath());
        } catch (Exception e) {
            log.error("JAR包存储目录初始化失败", e);
            throw new RuntimeException("JAR包存储目录初始化失败: " + e.getMessage(), e);
        }
    }

    @Override
    public R<JarUploadResult> uploadJarPackage(MultipartFile file) {
        List<JarUploadResult> results = batchUploadJarPackagesInternal(new MultipartFile[]{file});
        return R.success("上传完成", results.get(0));
    }

    /**
     * 批量上传JAR包（内部方法）
     * @param files 要上传的文件数组
     * @return 每个文件的上传结果列表
     */
    @Override
    public R<List<JarUploadResult>> batchUploadJarPackages(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return R.error("请选择至少一个文件");
        }

        List<JarUploadResult> results = batchUploadJarPackagesInternal(files);

        // 统计成功/失败数量
        long successCount = results.stream()
                .filter(r -> r.isFileUploadSuccess() && r.getRegistrationResults().stream().allMatch(CubeRegistrationResult::isSuccess))
                .count();

        long partialSuccessCount = results.stream()
                .filter(r -> r.isFileUploadSuccess() && r.getRegistrationResults().stream().anyMatch(rr -> !rr.isSuccess()))
                .count();

        long failureCount = results.size() - successCount - partialSuccessCount;

        String message = String.format("批量上传完成: 成功%d个, 部分成功%d个, 失败%d个",
                successCount, partialSuccessCount, failureCount);

        return R.success(message, results);
    }

    /**
     * 批量上传JAR包（内部实现）
     */
    private List<JarUploadResult> batchUploadJarPackagesInternal(MultipartFile[] files) {
        List<JarUploadResult> results = new ArrayList<>();

        for (MultipartFile file : files) {
            JarUploadResult result = new JarUploadResult();
            result.setFileUploadSuccess(false);

            try {
                validateUploadFile(file);

                // 生成唯一文件名
                String filename = CommonComponent.GuidAllocator().nextGUID().toString() + JAR_EXTENSION;
                Path targetPath = resolveStoragePath().resolve(filename);

                // 保存文件
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // 安装并注册Cube
                List<CubeRegistrationResult> registrationResults = cubeDomain.installAndRegisterCube(targetPath);

                // 设置结果
                result.setFilename(filename);
                result.setFileUploadSuccess(true);
                result.setRegistrationResults(registrationResults);
                result.setErrorMsg(null);

                log.info("JAR包上传成功: filename={}, size={}KB", filename, file.getSize() / 1024);

            } catch (IllegalArgumentException e) {
                String errorMsg = e.getMessage();
                result.setErrorMsg(errorMsg);
                log.warn("JAR包上传参数错误: {}", errorMsg);

            } catch (Exception e) {
                String errorMsg = "JAR包上传失败: " + e.getMessage();
                result.setErrorMsg(errorMsg);
                log.error(errorMsg, e);
            }

            results.add(result);
        }

        return results;
    }

    /**
     * 处理注册结果，记录失败情况
     */
    private void handleRegistrationResults(List<CubeRegistrationResult> results, String filename) {
        List<CubeRegistrationResult> failedResults = results.stream()
                .filter(result -> !result.isSuccess())
                .collect(Collectors.toList());

        if (!failedResults.isEmpty()) {
            String errorMsg = "JAR包上传成功，但部分Cube注册失败: " +
                    failedResults.stream()
                            .map(r -> r.getCubeId() + ": " + r.getMessage())
                            .collect(Collectors.joining("; "));

            log.warn("JAR包: {} - {}", filename, errorMsg);
        }
    }

    @Override
    public R<List<JarPackageInfo>> listJarPackages() {
        try {
            Path storageDir = resolveStoragePath();

            if (!Files.exists(storageDir)) {
                log.warn("JAR包存储目录不存在: {}", storageDir);
                return R.success("获取JAR包列表成功", List.of());
            }

            List<JarPackageInfo> jarPackages = Files.list(storageDir)
                    .filter(path -> path.toString().toLowerCase().endsWith(JAR_EXTENSION))
                    .map(this::buildJarPackageInfo)
                    .collect(Collectors.toList());

            log.debug("获取JAR包列表成功，共{}个文件", jarPackages.size());
            return R.success("获取JAR包列表成功", jarPackages);
        } catch (Exception e) {
            log.error("获取JAR包列表失败", e);
            return R.error("获取JAR包列表失败，请稍后重试");
        }
    }

    @Override
    public R<Void> renameJarPackage(String oldFilename, String newFilename) {
        try {
            validateFilename(oldFilename, "原文件名");
            validateFilename(newFilename, "新文件名");

            Path storageDir = resolveStoragePath();
            Path oldPath = storageDir.resolve(oldFilename);
            Path newPath = storageDir.resolve(ensureJarExtension(newFilename));

            if (!Files.exists(oldPath)) {
                return R.error("源文件不存在: " + oldFilename);
            }

            if (Files.exists(newPath) && !oldPath.equals(newPath)) {
                return R.error("目标文件名已存在: " + newFilename);
            }

            Files.move(oldPath, newPath);
            log.info("JAR包重命名成功: {} -> {}", oldFilename, newPath.getFileName());
            return R.success("JAR包重命名成功");
        } catch (IllegalArgumentException e) {
            log.warn("JAR包重命名参数错误: {}", e.getMessage());
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("JAR包重命名失败: {} -> {}", oldFilename, newFilename, e);
            return R.error("JAR包重命名失败，请稍后重试");
        }
    }

    @Override
    public R<Void> deleteJarPackage(String filename) {
        try {
            validateFilename(filename, "文件名");

            Path filePath = resolveStoragePath().resolve(filename);

            if (!Files.exists(filePath)) {
                return R.error("文件不存在: " + filename);
            }

            Files.delete(filePath);
            log.info("JAR包删除成功: {}", filename);
            return R.success("JAR包删除成功");
        } catch (IllegalArgumentException e) {
            log.warn("JAR包删除参数错误: {}", e.getMessage());
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("JAR包删除失败: {}", filename, e);
            return R.error("JAR包删除失败，请稍后重试");
        }
    }

    /**
     * 解析存储路径
     * 支持classpath:、file:等Spring资源路径格式
     */
    private Path resolveStoragePath() throws IOException {
        String storagePath = properties.getStoragePath();

        if (storagePath.startsWith("classpath:")) {
            // 处理classpath路径
            try {
                String classPathLocation = storagePath.substring("classpath:".length());
                if (classPathLocation.startsWith("/")) {
                    classPathLocation = classPathLocation.substring(1);
                }

                // 获取classpath根目录
                String classPath = this.getClass().getClassLoader().getResource("").getPath();
                if (classPath.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("windows")) {
                    classPath = classPath.substring(1);
                }

                return Paths.get(classPath, classPathLocation);
            } catch (Exception e) {
                log.warn("解析classpath路径失败，使用默认路径: {}", e.getMessage());
                return Paths.get(System.getProperty("java.io.tmpdir"), "mosaic", "plugins");
            }
        } else if (storagePath.startsWith("file:")) {
            // 处理file路径
            return Paths.get(storagePath.substring("file:".length()));
        } else {
            // 处理普通文件系统路径
            return Paths.get(storagePath);
        }
    }

    /**
     * 验证上传文件
     */
    private void validateUploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        if (!filename.toLowerCase().endsWith(JAR_EXTENSION)) {
            throw new IllegalArgumentException("只支持JAR文件格式");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过100MB");
        }
    }

    /**
     * 验证文件名
     */
    private void validateFilename(String filename, String fieldName) {
        if (!StringUtils.hasText(filename)) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }

        if (filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            throw new IllegalArgumentException(fieldName + "包含非法字符");
        }
    }

    /**
     * 确保文件名有.jar扩展名
     */
    private String ensureJarExtension(String filename) {
        return filename.toLowerCase().endsWith(JAR_EXTENSION) ? filename : filename + JAR_EXTENSION;
    }

    /**
     * 构建JAR包信息
     */
    private JarPackageInfo buildJarPackageInfo(Path jarPath) {
        try {
            JarPackageInfo info = new JarPackageInfo();
            info.setFilename(jarPath.getFileName().toString());
            info.setSize(Files.size(jarPath));
            info.setSizeKB(info.getSize() / 1024);
            info.setLastModified(LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(jarPath).toInstant(),
                    ZoneId.systemDefault()
            ));
            info.setFullPath(jarPath.toString());
            return info;
        } catch (IOException e) {
            log.error("获取JAR包信息失败: {}", jarPath, e);
            JarPackageInfo info = new JarPackageInfo();
            info.setFilename(jarPath.getFileName().toString());
            return info;
        }
    }
}