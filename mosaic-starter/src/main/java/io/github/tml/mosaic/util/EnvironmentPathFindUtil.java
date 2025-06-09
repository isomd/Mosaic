package io.github.tml.mosaic.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author welsir
 * @description :
 * @date 2025/6/9
 */
@Slf4j
public class EnvironmentPathFindUtil {

    private static final Path TEMP_DIR = getCurrentJarParentDir().resolve("mosaic/tmp/jar/");

    private static final String AGENT_JAR = "mosaic-agent-0.0.1-SNAPSHOT.jar";
    private static final String INSTALL_JAR = "mosaic-starter-1.0-SNAPSHOT.jar";

    // 环境类型枚举
    public enum EnvironmentType {
        DEVELOPMENT,  // 开发环境（在 IDE 或 mvn exec 中运行）
        DEPENDENCY,   // 作为依赖被引用
        PRODUCTION,   // 生产环境（打包部署）
        ;

        @Override
        public String toString() {
            return super.toString();
        }
    }

    /**
     * 自动检测当前运行环境
     */
    public static EnvironmentType detectEnvironment(Class<?> clazz) {
        try {
            // 获取类来源路径
            String sourcePath = getClassSourcePath(clazz).toLowerCase();

            // 1. 检查是否在开发环境中
            if (sourcePath.contains("target" + File.separator + "classes") ||
                    sourcePath.contains("build" + File.separator + "classes")) {
                return EnvironmentType.DEVELOPMENT;
            }

            // 2. 检查是否在 Maven/Gradle 依赖中
            if (sourcePath.contains(".m2") || sourcePath.contains(".gradle")) {
                return EnvironmentType.DEPENDENCY;
            }

            // 3. 默认视为生产环境
            return EnvironmentType.PRODUCTION;

        } catch (Exception e) {
            // 无法确定时默认为生产环境
            return EnvironmentType.PRODUCTION;
        }
    }

    /**
     * 获取 JAR 路径（自动环境适配）
     */
    public static String getJarPath(Class<?> anchorClass) {
        EnvironmentType env = EnvironmentType.PRODUCTION;
        log.info("检测到的部署环境："+env);
        switch (env) {
            case DEVELOPMENT:
                return resolveDevJarPath(anchorClass);
            case DEPENDENCY:
                return resolveDependencyJarPath(anchorClass);
            case PRODUCTION:
                return resolveProductionJarPath(anchorClass);
            default:
                throw new IllegalStateException("未知环境类型");
        }
    }

    /**
     * 获取类的来源路径
     */
    private static String getClassSourcePath(Class<?> clazz) throws URISyntaxException {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return fallbackToManifest(clazz);
        }
        return Paths.get(codeSource.getLocation().toURI()).toString();
    }

    /**
     * 解析开发环境 JAR 路径
     */
    private static String resolveDevJarPath(Class<?> anchorClass) {
        try {
            // 获取类所在目录
            String classPath = getClassSourcePath(anchorClass);
            Path classesDir = Paths.get(classPath);

            // 定位到 target 目录
            Path targetDir = classesDir.getParent();

            // 在 target 目录中查找最新的 JAR 文件
            Optional<File> latestJar = Arrays.stream(targetDir.toFile().listFiles())
                    .filter(file -> file.getName().endsWith(".jar"))
                    .filter(file -> isAnchorClassInJar(file, anchorClass))
                    .max(Comparator.comparingLong(File::lastModified));

            if (latestJar.isPresent()) {
                return latestJar.get().getAbsolutePath();
            }

            // 未找到时抛出异常
            throw new RuntimeException("未找到对应jar包，请检查路径是否正确");

        } catch (Exception e) {
            return fallbackToManifest(anchorClass);
        }
    }

    /**
     * 解析依赖 JAR 路径
     */
    private static String resolveDependencyJarPath(Class<?> anchorClass) {
        try {
            return getClassSourcePath(anchorClass);
        } catch (Exception e) {
            return fallbackToManifest(anchorClass);
        }
    }

    /**
     * 解析生产环境 JAR 路径
     */
    private static String resolveProductionJarPath(Class<?> anchorClass) {
        try {
            CodeSource codeSource = anchorClass.getProtectionDomain().getCodeSource();
            URL location = codeSource != null ? codeSource.getLocation() : null;

            if (location != null && location.getProtocol().equals("jar")) {

                String path = location.toString();
                if (path.contains("BOOT-INF/lib/") && path.endsWith(".jar!/")) {
                    return handleSpringBootFatJar(anchorClass);
                }
            }

            if (location != null && "file".equals(location.getProtocol()) && location.getPath().endsWith(".jar")) {
                return new File(location.toURI()).getAbsolutePath();
            }

            return location != null ? location.toString() : fallbackToManifest(anchorClass);

        } catch (Exception e) {
            return fallbackToManifest(anchorClass);
        }
    }

    private static String handleSpringBootFatJar(Class<?> targetClass) {
        ClassLoader cl = targetClass.getClassLoader();
        if ("org.springframework.boot.loader.LaunchedURLClassLoader".contains(cl.getClass().getName())) {
            try {
                if(targetClass.getSimpleName().toLowerCase().contains("agent")){
                    return extractJarIfNeeded(AGENT_JAR);
                }else {
                    return extractJarIfNeeded(INSTALL_JAR);
                }
            } catch (Exception e) {
                throw new RuntimeException("无法获取 LaunchedURLClassLoader 的 URLs", e);
            }
        }
        return null;
    }

    public static String extractJarIfNeeded(String targetJarName) {
        try {
            Path targetPath = TEMP_DIR.resolve(targetJarName);
            log.info("targetPath: {}", targetPath);
            // 2. 定位 fat jar 本体
            String fatJarPath = getFatJarPathFromClasspath();

            if(fatJarPath==null){
                throw new RuntimeException("无法找到当前jar包路径");
            }

            log.info("fatJarPath :{}",fatJarPath);

            try (JarFile jarFile = new JarFile(fatJarPath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                log.info("entries :{}",entries);
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.startsWith("BOOT-INF/lib/") && name.endsWith(targetJarName)) {
                        // 创建输出目录
                        Files.createDirectories(TEMP_DIR);

                        // 3. 解压 jar 文件到临时目录
                        try (InputStream is = jarFile.getInputStream(entry)) {
                            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        return targetPath.toAbsolutePath().toString();
                    }
                }
            }

            throw new FileNotFoundException("未找到 jar: " + targetJarName + " in fat jar");

        } catch (Exception e) {
            throw new RuntimeException("提取 jar 时出错：" + targetJarName, e);
        }
    }

    public static String getFatJarPathFromClasspath() {
        String classPath = System.getProperty("java.class.path");
        if (classPath != null && classPath.endsWith(".jar")) {
            return new File(classPath).getAbsolutePath();
        }
        return null;
    }


    /**
     * 解析 Spring Boot 环境中的模块 JAR 路径
     */
    public static String resolveSpringBootJarPath(Class<?> anchorClass) {
        ProtectionDomain domain = anchorClass.getProtectionDomain();
        CodeSource source = domain.getCodeSource();
        if (source == null) {
            return fallbackToManifest(anchorClass);
        }

        URL location = source.getLocation();
        if (location == null) {
            return fallbackToManifest(anchorClass);
        }

        String path = location.toString();
        if (path.startsWith("jar:file:") && path.contains("!/BOOT-INF/lib/")) {
            return path;
        } else if (path.startsWith("file:")) {
            return path;
        } else {
            return fallbackToManifest(anchorClass);
        }
    }

    /**
     * 回退到 Manifest 方式获取路径
     */
    private static String fallbackToManifest(Class<?> anchorClass) {
        try {
            // 获取 Manifest
            Manifest manifest = new Manifest(anchorClass.getResourceAsStream("/META-INF/MANIFEST.MF"));
            Attributes attributes = manifest.getMainAttributes();

            // 尝试从 Manifest 获取路径
            String classPath = attributes.getValue("Class-Path");
            if (classPath != null) {
                String[] paths = classPath.split(" ");
                for (String path : paths) {
                    if (path.endsWith(".jar")) {
                        return new File(path).getAbsolutePath();
                    }
                }
            }

            // 尝试从系统属性获取
            return System.getProperty("java.class.path").split(File.pathSeparator)[0];

        } catch (Exception e) {
            throw new RuntimeException("无法确定 JAR 路径", e);
        }
    }

    /**
     * 检查 JAR 是否包含锚点类
     */
    private static boolean isAnchorClassInJar(File jarFile, Class<?> anchorClass) {
        try (JarFile jar = new JarFile(jarFile)) {
            String classPath = anchorClass.getName().replace('.', '/') + ".class";
            return jar.getEntry(classPath) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static Path getCurrentJarParentDir() {
        try {
            CodeSource codeSource = EnvironmentPathFindUtil.class.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                throw new IllegalStateException("无法获取当前运行 JAR 的路径");
            }

            URI location = codeSource.getLocation().toURI();
            String scheme = location.getScheme();

            if ("jar".equals(scheme)) {
                String raw = location.toString();
                int sepIndex = raw.indexOf("!/");
                if (sepIndex != -1) {
                    raw = raw.substring(4, sepIndex); // 去掉 jar: 和 !/ 之后的内容
                    location = URI.create(raw);
                }
            }

            Path jarPath = Paths.get(location);
            Path parentDir = jarPath.getParent();

            if (parentDir == null) {
                throw new IllegalStateException("JAR 包没有上级目录？！");
            }

            return parentDir;

        } catch (Exception e) {
            throw new RuntimeException("获取当前 JAR 所在目录失败", e);
        }
    }

}