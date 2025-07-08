package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.config.properties.MosaicPluginProperties;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import io.github.tml.mosaic.domain.cube.CubeDomain;
import io.github.tml.mosaic.entity.dto.JarPkgDTO;
import io.github.tml.mosaic.entity.enums.JarInstallStatus;
import io.github.tml.mosaic.entity.vo.JarInstallResult;
import io.github.tml.mosaic.entity.vo.JarOperationResult;
import io.github.tml.mosaic.entity.vo.JarUploadResult;
import io.github.tml.mosaic.service.JarPkgService;
import io.github.tml.mosaic.util.FilenameUtil;
import io.github.tml.mosaic.util.R;
import io.github.tml.mosaic.util.ShortUuidUtil;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JarPkgServiceImpl implements JarPkgService {

    private final MosaicPluginProperties properties;
    private final CubeDomain cubeDomain;

    /**
     * JAR包状态管理Map - 线程安全
     * Key: JAR包ID, Value: JAR包信息
     */
    private final Map<String, JarPkgDTO> jarPackageMap = new ConcurrentHashMap<>();

    private static final String JAR_EXTENSION = ".jar";
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

    @PostConstruct
    public void initDirectory() {
        try {
            Path storageDir = resolveStoragePath();
            Files.createDirectories(storageDir);
            loadExistingJarPackages();
            log.info("JAR包存储目录初始化完成: {}", storageDir.toAbsolutePath());
        } catch (Exception e) {
            log.error("JAR包存储目录初始化失败", e);
            throw new RuntimeException("JAR包存储目录初始化失败: " + e.getMessage(), e);
        }
    }

    // ==================== 文件管理操作 ====================

    @Override
    public R<List<JarUploadResult>> batchUploadJarPackages(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return R.error("请选择至少一个文件");
        }

        List<JarUploadResult> results = batchUploadJarPackagesInternal(files);
        long successCount = results.stream().filter(JarUploadResult::isUploadSuccess).count();
        long failureCount = results.size() - successCount;

        String message = String.format("批量上传完成: 成功%d个, 失败%d个", successCount, failureCount);
        return R.success(message, results);
    }

    @Override
    public R<List<JarPkgDTO>> listJarPackages() {
        try {
            List<JarPkgDTO> jarPackages = new ArrayList<>(jarPackageMap.values());
            jarPackages.sort(Comparator.comparing(JarPkgDTO::getUploadTime).reversed());

            log.debug("获取JAR包列表成功，共{}个文件", jarPackages.size());
            return R.success("获取JAR包列表成功", jarPackages);
        } catch (Exception e) {
            log.error("获取JAR包列表失败", e);
            return R.error("获取JAR包列表失败，请稍后重试");
        }
    }

    @Override
    public R<JarOperationResult> renameJarPackage(String jarId, String newFilename) {
        try {
            validateJarId(jarId);
            FilenameUtil.validateFilename(newFilename, "新文件名");

            JarPkgDTO jarInfo = jarPackageMap.get(jarId);
            if (jarInfo == null) {
                return R.error("JAR包不存在: " + jarId);
            }

            String newFilenameWithExt = FilenameUtil.ensureJarExtension(newFilename);

            // 检查新文件名是否已存在
            boolean nameExists = jarPackageMap.values().stream()
                    .anyMatch(info -> newFilenameWithExt.equals(info.getFilename()) && !jarId.equals(info.getId()));

            if (nameExists) {
                return R.error("文件名已存在: " + newFilenameWithExt);
            }

            // 生成新的存储文件名
            String newStorageFilename = FilenameUtil.generateNewStorageFilename(jarInfo.getStorageFilename(), newFilenameWithExt);

            // 重命名物理文件
            Path storageDir = resolveStoragePath();
            Path oldPath = storageDir.resolve(jarInfo.getStorageFilename());
            Path newPath = storageDir.resolve(newStorageFilename);

            if (Files.exists(oldPath)) {
                Files.move(oldPath, newPath);
                log.info("物理文件重命名成功: {} -> {}", jarInfo.getStorageFilename(), newStorageFilename);
            }

            // 更新JAR包信息
            jarInfo.setFilename(newFilenameWithExt.split("\\.")[0]);
            jarInfo.setStorageFilename(newStorageFilename);
            jarInfo.setLastModified(LocalDateTime.now());
            jarInfo.setFullPath(newPath.toString());

            JarOperationResult result = new JarOperationResult();
            result.setJarId(jarId);
            result.setSuccess(true);

            log.info("JAR包重命名成功: {} -> {}", jarId, newFilenameWithExt);
            return R.success("JAR包重命名成功", result);
        } catch (Exception e) {
            log.error("JAR包重命名失败: {} -> {}", jarId, newFilename, e);
            return R.error("JAR包重命名失败: " + e.getMessage());
        }
    }

    @Override
    public R<List<JarOperationResult>> batchDeleteJarPackages(List<String> jarIds) {
        List<JarOperationResult> results = new ArrayList<>();

        for (String jarId : jarIds) {
            JarOperationResult result = new JarOperationResult();
            result.setJarId(jarId);

            try {
                validateJarId(jarId);

                JarPkgDTO jarInfo = jarPackageMap.get(jarId);
                if (jarInfo == null) {
                    result.setSuccess(false);
                    result.setErrorMessage("JAR包不存在: " + jarId);
                    results.add(result);
                    continue;
                }

                // 删除物理文件
                Path filePath = resolveStoragePath().resolve(jarInfo.getStorageFilename());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("物理文件删除成功: {}", jarInfo.getStorageFilename());
                }

                // 从状态管理Map中移除
                jarPackageMap.remove(jarId);

                result.setSuccess(true);
                log.info("JAR包删除成功: {}", jarId);

            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage("删除失败: " + e.getMessage());
                log.error("JAR包删除失败: {}", jarId, e);
            }

            results.add(result);
        }

        long successCount = results.stream().filter(JarOperationResult::isSuccess).count();
        long failureCount = results.size() - successCount;
        String message = String.format("批量删除完成: 成功%d个, 失败%d个", successCount, failureCount);

        return R.success(message, results);
    }

    // ==================== Cube定义管理操作 ====================

    @Override
    public R<List<JarInstallResult>> batchInstallJarPackages(List<String> jarIds) {
        List<JarInstallResult> results = new ArrayList<>();

        for (String jarId : jarIds) {
            JarInstallResult result = new JarInstallResult();
            result.setJarId(jarId);

            try {
                validateJarId(jarId);

                JarPkgDTO jarInfo = jarPackageMap.get(jarId);
                if (jarInfo == null) {
                    result.setInstallSuccess(false);
                    result.setErrorMessage("JAR包不存在: " + jarId);
                    results.add(result);
                    continue;
                }

                if (JarInstallStatus.INSTALLED.equals(jarInfo.getInstallStatus())) {
                    result.setInstallSuccess(true);
                    result.setErrorMessage("JAR包已安装");
                    results.add(result);
                    continue;
                }

                // 执行安装
                Path jarPath = resolveStoragePath().resolve(jarInfo.getStorageFilename());
                List<CubeRegistrationResult> registrationResults = cubeDomain.installAndRegisterCube(jarPath);

                // 更新状态
                boolean allSuccess = registrationResults.stream().allMatch(CubeRegistrationResult::isSuccess);
                jarInfo.setInstallStatus(allSuccess ? JarInstallStatus.INSTALLED : JarInstallStatus.INSTALL_FAILED);
                jarInfo.setInstallTime(LocalDateTime.now());

                if (!allSuccess) {
                    String errorMsg = registrationResults.stream()
                            .filter(r -> !r.isSuccess())
                            .map(CubeRegistrationResult::getMessage)
                            .collect(Collectors.joining("; "));
                    jarInfo.setErrorMessage(errorMsg);
                } else {
                    jarInfo.setErrorMessage(null);
                }

                result.setInstallSuccess(allSuccess);
                result.setRegistrationResults(registrationResults);
                if (!allSuccess) {
                    result.setErrorMessage(jarInfo.getErrorMessage());
                }

                log.info("JAR包安装{}: {} ({})", allSuccess ? "成功" : "失败", jarId, jarInfo.getFilename());

            } catch (Exception e) {
                JarPkgDTO jarInfo = jarPackageMap.get(jarId);
                if (jarInfo != null) {
                    jarInfo.setInstallStatus(JarInstallStatus.INSTALL_FAILED);
                    jarInfo.setErrorMessage(e.getMessage());
                }

                result.setInstallSuccess(false);
                result.setErrorMessage("安装失败: " + e.getMessage());
                log.error("JAR包安装失败: {}", jarId, e);
            }

            results.add(result);
        }

        long successCount = results.stream().filter(JarInstallResult::isInstallSuccess).count();
        long failureCount = results.size() - successCount;
        String message = String.format("批量安装完成: 成功%d个, 失败%d个", successCount, failureCount);

        return R.success(message, results);
    }

    // ==================== 私有方法 ====================

    /**
     * 批量上传JAR包内部实现
     */
    private List<JarUploadResult> batchUploadJarPackagesInternal(MultipartFile[] files) {
        List<JarUploadResult> results = new ArrayList<>();

        for (MultipartFile file : files) {
            JarUploadResult result = new JarUploadResult();
            result.setOriginalFilename(file.getOriginalFilename());

            try {
                validateUploadFile(file);

                // 生成JAR包ID
                String jarId = ShortUuidUtil.generateShortId();

                // 生成存储文件名
                String storageFilename = FilenameUtil.generateStorageFilename(file.getOriginalFilename());
                Path targetPath = resolveStoragePath().resolve(storageFilename);

                // 保存文件
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // 创建JAR包信息
                JarPkgDTO jarInfo = buildJarPackageInfo(jarId, Objects.requireNonNull(file.getOriginalFilename()), storageFilename, targetPath, file.getSize());

                // 添加到状态管理Map
                jarPackageMap.put(jarId, jarInfo);

                result.setJarId(jarId);
                result.setUploadSuccess(true);

                log.info("JAR包上传成功: jarId={}, filename={}, storageFilename={}, size={}KB",
                        jarId, file.getOriginalFilename(), storageFilename, file.getSize() / 1024);

            } catch (Exception e) {
                result.setUploadSuccess(false);
                result.setErrorMessage(e.getMessage());
                log.error("JAR包上传失败: {}", file.getOriginalFilename(), e);
            }

            results.add(result);
        }

        return results;
    }

    /**
     * 加载已存在的JAR包
     */
    private void loadExistingJarPackages() {
        try {
            Path storageDir = resolveStoragePath();
            if (!Files.exists(storageDir)) {
                return;
            }

            Files.list(storageDir)
                    .filter(path -> path.toString().toLowerCase().endsWith(JAR_EXTENSION))
                    .forEach(this::loadExistingJarPackage);

            log.info("load exist jar package :{}", jarPackageMap.size());
        } catch (Exception e) {
            log.error("load exist jar package fail", e);
        }
    }

    /**
     * 加载单个已存在的JAR包
     */
    private void loadExistingJarPackage(Path jarPath) {
        try {
            String storageFilename = jarPath.getFileName().toString();
            String jarId = ShortUuidUtil.generateShortId();

            // 尝试从存储文件名中提取原始文件名
            String originalFilename = extractOriginalFilenameFromStorage(storageFilename);

            JarPkgDTO jarInfo = buildJarPackageInfo(jarId, originalFilename, storageFilename, jarPath, Files.size(jarPath));

            jarPackageMap.put(jarId, jarInfo);
            log.debug("加载已存在JAR包: {} -> {}", storageFilename, originalFilename);
        } catch (Exception e) {
            log.warn("加载JAR包失败: {}", jarPath, e);
        }
    }

    /**
     * 从存储文件名中提取原始文件名
     */
    private String extractOriginalFilenameFromStorage(String storageFilename) {
        String nameWithoutExt = FilenameUtil.removeJarExtension(storageFilename);
        int lastUnderscoreIndex = nameWithoutExt.lastIndexOf('_');

        if (lastUnderscoreIndex > 0) {
            String possibleUuid = nameWithoutExt.substring(lastUnderscoreIndex + 1);
            // 检查是否为8位UUID格式
            if (possibleUuid.length() == 8 && possibleUuid.matches("[a-f0-9]{8}")) {
                return nameWithoutExt.substring(0, lastUnderscoreIndex) + JAR_EXTENSION;
            }
        }

        // 如果无法提取，直接返回存储文件名
        return storageFilename;
    }

    /**
     * 构建JAR包信息
     */
    private JarPkgDTO buildJarPackageInfo(String jarId, String filename, String storageFilename, Path jarPath, long fileSize) throws IOException {
        JarPkgDTO info = new JarPkgDTO();
        info.setId(jarId);
        info.setFilename(filename.split("\\.")[0]);
        info.setStorageFilename(storageFilename);
        info.setSize(fileSize);
        info.setSizeKB(fileSize / 1024);
        info.setUploadTime(LocalDateTime.now());
        info.setLastModified(LocalDateTime.ofInstant(
                Files.getLastModifiedTime(jarPath).toInstant(),
                ZoneId.systemDefault()));
        info.setInstallStatus(JarInstallStatus.UNINSTALLED);
        info.setFullPath(jarPath.toString());

        return info;
    }

    /**
     * 解析存储路径
     */
    private Path resolveStoragePath() throws IOException {
        String storagePath = properties.getStoragePath();

        if (storagePath.startsWith("classpath:")) {
            try {
                String classPathLocation = storagePath.substring("classpath:".length());
                if (classPathLocation.startsWith("/")) {
                    classPathLocation = classPathLocation.substring(1);
                }

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
            return Paths.get(storagePath.substring("file:".length()));
        } else {
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
     * 验证JAR包ID
     */
    private void validateJarId(String jarId) {
        if (!StringUtils.hasText(jarId)) {
            throw new IllegalArgumentException("JAR包ID不能为空");
        }
    }
}