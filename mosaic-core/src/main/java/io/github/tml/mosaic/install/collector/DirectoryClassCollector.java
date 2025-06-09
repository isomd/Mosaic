package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.collector.core.InfoCollector;
import io.github.tml.mosaic.install.support.InfoContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 描述: 本地目录类收集器
 * @author suifeng
 * 日期: 2025/6/9
 */
public class DirectoryClassCollector implements InfoCollector {

    @Override
    public void collect(InfoContext infoContext) {
        Resource resource = infoContext.getResource();
        String dirPath = resource.getPath();
        if (dirPath.startsWith("file:")) {
            dirPath = dirPath.substring("file:".length() + 1);
        }
        if (!isValidDirectory(dirPath)) {
            throw new CubeException("无效的代码目录: " + dirPath);
        }

        List<Class<?>> classes = scanClasses(dirPath);
        infoContext.setAllClazz(classes);
    }

    /**
     * 使用Java NIO扫描类文件
     */
    private List<Class<?>> scanClasses(String rootPath) {
        List<Class<?>> classes = new ArrayList<>();
        Path rootDir = Paths.get(rootPath);
        Path scanPath = rootDir.resolve("");

        if (!Files.exists(scanPath)) {
            throw new CubeException("扫描路径不存在: " + scanPath);
        }

        try (Stream<Path> paths = Files.walk(scanPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class"))
                    .forEach(p -> loadClassFromPath(p, rootDir)
                            .ifPresent(classes::add));
        } catch (IOException e) {
            throw new CubeException("目录扫描失败: " + scanPath, e);
        }
        return classes;
    }

    /**
     * 从路径加载类
     */
    private Optional<Class<?>> loadClassFromPath(Path classPath, Path rootDir) {
        try {
            // 计算相对路径并转换为类名
            String relativePath = rootDir.relativize(classPath).toString();
            String className = relativePath
                    .replace(File.separatorChar, '.')
                    .replace(".class", "");

            // 加载类
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            return Optional.of(cl.loadClass(className));
        } catch (ClassNotFoundException e) {
            System.err.println("类加载失败: " + classPath);
            return Optional.empty();
        } catch (InvalidPathException e) {
            System.err.println("路径转换失败: " + classPath);
            return Optional.empty();
        }
    }

    /**
     * 验证目录有效性
     */
    private boolean isValidDirectory(String path) {
        if (path == null) return false;
        File dir = new File(path);
        return dir.exists() && dir.isDirectory();
    }
}