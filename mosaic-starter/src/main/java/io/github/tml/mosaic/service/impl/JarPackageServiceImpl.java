package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.config.properties.MosaicPluginProperties;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinitionConverter;
import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.entity.JarPackageInfo;
import io.github.tml.mosaic.install.install.InfoContextInstaller;
import io.github.tml.mosaic.install.reader.ReaderType;
import io.github.tml.mosaic.install.domian.InfoContext;
import io.github.tml.mosaic.service.JarPackageService;
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
    private final InfoContextInstaller installer;
    private final CubeContext cubeContext;

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
    public String uploadJarPackage(MultipartFile file) throws IOException {
        validateUploadFile(file);

        String filename = CommonComponent.GuidAllocator().nextGUID().toString() + JAR_EXTENSION;
        Path targetPath = null;
        targetPath = resolveStoragePath().resolve(filename);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 直接调用安装器安装
            String[] strings = new String[1];
            strings[0] = ReaderType.FILE.getPrefix() + targetPath;
            List<InfoContext> infoContexts = installer.installCubeInfoContext(strings);

            // 将安装项转换成CubeDefinition列表
            List<CubeDefinition> cubeDefinitions = new ArrayList<>();
            if (infoContexts != null && !infoContexts.isEmpty()) {
                for (InfoContext infoContext : infoContexts) {
                    cubeDefinitions.addAll(CubeDefinitionConverter.convertToCubeDefinitions(infoContext));
                }
            }

            // 注册进context容器
            for (CubeDefinition cubeDefinition : cubeDefinitions) {
                cubeContext.registerCubeDefinition(new GUUID(cubeDefinition.getId()), cubeDefinition);
            }

            log.info("JAR包上传成功: filename={}, size={}KB", filename, file.getSize() / 1024);
            return filename;
        } catch (IOException e) {
            log.error("JAR包文件写入失败: {}", filename, e);
            throw new RuntimeException("JAR包上传失败：文件写入错误");
        }
    }

    @Override
    public List<JarPackageInfo> listJarPackages() throws IOException {
        Path storageDir = resolveStoragePath();

        if (!Files.exists(storageDir)) {
            log.warn("JAR包存储目录不存在: {}", storageDir);
            return List.of();
        }

        try {
            return Files.list(storageDir)
                    .filter(path -> path.toString().toLowerCase().endsWith(JAR_EXTENSION))
                    .map(this::buildJarPackageInfo)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("读取JAR包目录失败: {}", storageDir, e);
            throw new RuntimeException("读取JAR包目录失败");
        }
    }

    @Override
    public void renameJarPackage(String oldFilename, String newFilename) throws IOException {
        validateFilename(oldFilename, "原文件名");
        validateFilename(newFilename, "新文件名");

        Path storageDir = resolveStoragePath();
        Path oldPath = storageDir.resolve(oldFilename);
        Path newPath = storageDir.resolve(ensureJarExtension(newFilename));

        if (!Files.exists(oldPath)) {
            throw new IllegalArgumentException("源文件不存在: " + oldFilename);
        }

        if (Files.exists(newPath) && !oldPath.equals(newPath)) {
            throw new IllegalArgumentException("目标文件名已存在: " + newFilename);
        }

        try {
            Files.move(oldPath, newPath);
            log.info("JAR包重命名成功: {} -> {}", oldFilename, newPath.getFileName());
        } catch (IOException e) {
            log.error("JAR包重命名失败: {} -> {}", oldFilename, newFilename, e);
            throw new RuntimeException("JAR包重命名失败：文件系统错误");
        }
    }

    @Override
    public void deleteJarPackage(String filename) throws IOException {
        validateFilename(filename, "文件名");

        Path filePath = resolveStoragePath().resolve(filename);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("文件不存在: " + filename);
        }

        try {
            Files.delete(filePath);
            log.info("JAR包删除成功: {}", filename);
        } catch (IOException e) {
            log.error("JAR包删除失败: {}", filename, e);
            throw new RuntimeException("JAR包删除失败：文件系统错误");
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