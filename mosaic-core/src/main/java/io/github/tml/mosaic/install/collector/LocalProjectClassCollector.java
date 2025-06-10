package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.collector.core.InfoCollector;
import io.github.tml.mosaic.install.support.InfoContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * 描述: 本地项目类收集器
 * @author suifeng
 * 日期: 2025/6/9
 */
@Slf4j
public class LocalProjectClassCollector implements InfoCollector {

    @Override
    public void collect(InfoContext infoContext) {
        String mainClassPackage = getMainClassPackage();
        try {
            scanAllClass(mainClassPackage, infoContext);
        }catch (Exception e){
            throw new CubeException(String.format("%s collect error: %s", this.getClass().getName(), e.getMessage()));
        }

    }

    /**
     * 通过堆栈信息获取主类所在的包
     */
    private String getMainClassPackage() throws CubeException {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if ("main".equals(element.getMethodName())) {
                try {
                    Class<?> mainClass = Class.forName(element.getClassName());
                    Package pkg = mainClass.getPackage();
                    if (pkg != null) {
                        return pkg.getName();
                    }
                } catch (ClassNotFoundException ignored) {
                    log.error("cannot find main class: {}", element.getClassName());
                    break;
                }
            }
        }
        throw new CubeException("cannot find main class");
    }

    private void scanAllClass(String packageName, InfoContext infoContext) throws Exception{
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        infoContext.setClassLoader(classLoader);
        String path = packageName.replace(".", "/");

        Enumeration<URL> resources = classLoader.getResources(path);
        while(resources.hasMoreElements()){
            URL resource = resources.nextElement();
            processResource(resource, packageName, classLoader, classes);
        }

        if (classLoader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) classLoader).getURLs();
            for (URL url : urls) {
//                processResource(url, packageName, classLoader, classes);
            }
        }

        infoContext.setAllClazz(new ArrayList<>(classes));
    }

    private void processResource(URL resource, String basePackage,
                                        ClassLoader classLoader, Set<Class<?>> classes) throws Exception {
        if ("file".equals(resource.getProtocol())) {
            File file = new File(resource.toURI());
            if (file.isDirectory()) {
                scanDirectory(file, basePackage, classLoader, classes);
            } else if (file.getName().endsWith(".jar")) {
                scanJar(file, basePackage, classLoader, classes);
            }
        } else if ("jar".equals(resource.getProtocol())) {
            String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
            scanJar(new File(jarPath), basePackage, classLoader, classes);
        }
    }

    private void scanDirectory(File directory, String basePackage,
                                      ClassLoader classLoader, Set<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, basePackage + "." + file.getName(), classLoader, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                addClass(className, classLoader, classes);
            }
        }
    }

    private void scanJar(File jarFile, String basePackage,
                                ClassLoader classLoader, Set<Class<?>> classes) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            String packagePath = basePackage.replace('.', '/');
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // 处理普通类文件
                if (entryName.endsWith(".class") && entryName.startsWith(packagePath)) {
                    String className = entryName.replace('/', '.')
                            .substring(0, entryName.length() - 6);
                    addClass(className, classLoader, classes);
                }
                // 处理Spring Boot嵌套JAR
                else if (isSpringBootJar(entryName)) {
                    processNestedJar(jar, entry, basePackage, classLoader, classes);
                }
            }
        }
    }

    private void addClass(String className, ClassLoader classLoader, Set<Class<?>> classes) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            classes.add(clazz);
        } catch (ClassNotFoundException | NoClassDefFoundError |
                 UnsupportedClassVersionError e) {
            System.err.println("无法加载类: " + className + " | 原因: " + e.getMessage());
        }
    }

    private boolean isSpringBootJar(String entryName) {
        return entryName.startsWith("BOOT-INF/lib/") && entryName.endsWith(".jar");
    }

    private void processNestedJar(JarFile outerJar, JarEntry nestedEntry,
                                         String basePackage, ClassLoader classLoader,
                                         Set<Class<?>> classes) throws Exception {
        // 创建嵌套JAR的临时副本
        File tempFile = File.createTempFile("nested-", ".jar");
        try (InputStream in = outerJar.getInputStream(nestedEntry);
             FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        // 扫描嵌套JAR
        scanJar(tempFile, basePackage, classLoader, classes);
        tempFile.delete(); // 清理临时文件
    }

}