package io.github.tml.mosaic.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
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
public class EnvironmentPathFindUtil {

    // 环境类型枚举
    public enum EnvironmentType {
        DEVELOPMENT,  // 开发环境（在 IDE 或 mvn exec 中运行）
        DEPENDENCY,   // 作为依赖被引用
        PRODUCTION,   // 生产环境（打包部署）
        SPRING_BOOT   // Spring Boot 胖 JAR 环境
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

            // 2. 检查是否在 Spring Boot 胖 JAR 中
            if (isSpringBootJar(clazz)) {
                return EnvironmentType.SPRING_BOOT;
            }

            // 3. 检查是否在 Maven/Gradle 依赖中
            if (sourcePath.contains(".m2") || sourcePath.contains(".gradle")) {
                return EnvironmentType.DEPENDENCY;
            }

            // 4. 默认视为生产环境
            return EnvironmentType.PRODUCTION;

        } catch (Exception e) {
            // 无法确定时默认为生产环境
            return EnvironmentType.PRODUCTION;
        }
    }

    /**
     * 智能获取 JAR 路径（全自动环境适配）
     */
    public static String getJarPath(Class<?> anchorClass) {
        EnvironmentType env = detectEnvironment(anchorClass);

        switch (env) {
            case DEVELOPMENT:
                return resolveDevJarPath(anchorClass);

            case DEPENDENCY:
                return resolveDependencyJarPath(anchorClass);

            case PRODUCTION:
                return resolveProductionJarPath(anchorClass);

            case SPRING_BOOT:
                return resolveSpringBootJarPath(anchorClass);

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

            // 未找到时尝试构建路径
            return targetDir.resolve(guessJarName(anchorClass)).toString();

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
            String path = getClassSourcePath(anchorClass);

            // 处理 Docker 符号链接
            if (isDockerEnvironment()) {
                File jarFile = new File(path);
                if (jarFile.exists() && jarFile.isFile()) {
                    return jarFile.getAbsolutePath();
                }
                // 尝试从环境变量获取
                String dockerPath = System.getenv("APP_JAR_PATH");
                if (dockerPath != null) {
                    return dockerPath;
                }
            }

            return path;

        } catch (Exception e) {
            return fallbackToManifest(anchorClass);
        }
    }

    /**
     * 解析 Spring Boot 环境中的模块 JAR 路径
     */
    private static String resolveSpringBootJarPath(Class<?> anchorClass) {
        try {
            // 获取主 JAR 路径
            String mainJarPath = getClassSourcePath(anchorClass);

            try (JarFile jar = new JarFile(mainJarPath)) {
                // 在 BOOT-INF/lib 中查找包含锚点类的 JAR
                return jar.stream()
                        .filter(entry -> entry.getName().startsWith("BOOT-INF/lib/"))
                        .filter(entry -> entry.getName().endsWith(".jar"))
                        .map(entry -> "file:" + mainJarPath + "!/" + entry.getName())
                        .filter(jarUrl -> containsClass(jarUrl, anchorClass))
                        .findFirst()
                        .orElse(mainJarPath);
            }

        } catch (Exception e) {
            return fallbackToManifest(anchorClass);
        }
    }

    /**
     * 检查是否为 Spring Boot 环境
     */
    private static boolean isSpringBootJar(Class<?> clazz) {
        return clazz.getResource("/BOOT-INF/classes/") != null;
    }

    /**
     * 检查是否为 Docker 环境
     */
    private static boolean isDockerEnvironment() {
        return System.getenv().containsKey("DOCKER_CONTAINER") ||
                Files.exists(Paths.get("/.dockerenv"));
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
     * 智能推测 JAR 文件名
     */
    private static String guessJarName(Class<?> anchorClass) {
        String simpleName = anchorClass.getSimpleName();
        if (simpleName.endsWith("Application") || simpleName.endsWith("Main")) {
            return simpleName.substring(0, simpleName.length() - 11) + ".jar";
        }
        return anchorClass.getPackage().getName() + ".jar";
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

    /**
     * 检查 JAR URL 是否包含指定类
     */
    private static boolean containsClass(String jarUrl, Class<?> clazz) {
        // 简化实现 - 实际中需要使用自定义类加载器
        return jarUrl.contains(clazz.getSimpleName().toLowerCase());
    }

    /**
     * 获取环境类型描述
     */
    public static String getEnvironmentDescription(Class<?> anchorClass) {
        EnvironmentType env = detectEnvironment(anchorClass);
        switch (env) {
            case DEVELOPMENT: return "开发环境";
            case DEPENDENCY: return "依赖引用";
            case PRODUCTION: return "生产部署";
            case SPRING_BOOT: return "Spring Boot 胖 JAR";
            default: return "未知环境";
        }
    }
}