package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.install.collector.core.InfoCollector;
import io.github.tml.mosaic.install.support.InfoContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 描述: 本地项目类收集器
 * @author suifeng
 * 日期: 2025/6/9
 */
@Slf4j
public class LocalProjectClassCollector implements InfoCollector {

    private String basePackage;
    private String basePackagePath;

    @Override
    public void collect(InfoContext infoContext) {
        long startTime = System.currentTimeMillis();
        try {
            basePackage = getMainClassPackage();
            basePackagePath = basePackage.replace('.', '/');
            Set<Class<?>> classes = scanProjectClasses();
            log.info("collect local project class :{} time: {}ms", classes.size(), System.currentTimeMillis() - startTime);
            for (Class<?> aClass : classes) {
                System.out.println(aClass);
            }
            infoContext.setAllClazz(new ArrayList<>(classes));
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

    private Set<Class<?>> scanProjectClasses() throws Exception {
        Set<Class<?>> classes = new LinkedHashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // 扫描主项目（当前模块）
        scanPackage(basePackage, classLoader, classes);

        return classes;
    }

    private void scanPackage(String packageName, ClassLoader classLoader, Set<Class<?>> classes) throws Exception {
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (isJarURL(resource)) {
                processJarResource(resource, packageName, classes);
            } else {
                processFileResource(resource, packageName, classes);
            }
        }
    }

    private void processJarResource(URL resource, String packageName, Set<Class<?>> classes) throws Exception {
        JarFile jarFile =  getJarFile(resource);
        try {
            String prefix = packageName.replace('.', '/') + "/";
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // 检查是否是类文件且在基础包路径下
                if (entryName.endsWith(".class") &&
                        entryName.startsWith(basePackagePath) &&
                        !entryName.contains("$")) { // 排除内部类

                    String className = entryName.replace('/', '.')
                            .substring(0, entryName.length() - 6);
                    loadClass(className, classes);
                }
            }
        } finally {
            jarFile.close();
        }
    }

    private void processFileResource(URL resource, String packageName, Set<Class<?>> classes) {
        File directory = new File(resource.getFile());
        if (directory.exists() && directory.isDirectory()) {
            scanDirectory(directory, packageName, classes);
        }
    }

    private void scanDirectory(File directory, String packageName, Set<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // 递归扫描子目录
                String subPackage = packageName + '.' + file.getName();
                scanDirectory(file, subPackage, classes);
            } else if (file.getName().endsWith(".class")) {
                // 转换文件路径为类名
                String className = packageName + '.' +
                        file.getName().replace(".class", "");
                loadClass(className, classes);
            }
        }
    }

    private void loadClass(String className, Set<Class<?>> classes) {
        try {
            // 只加载属于基础包或其子包的类
            if (className.startsWith(basePackage)) {
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load class: " + className);
        }
    }

    private boolean isJarURL(URL url) {
        String protocol = url.getProtocol();
        return "jar".equals(protocol) || "war".equals(protocol) || url.toString().contains(".jar!");
    }

    private JarFile getJarFile(URL url) throws IOException {
        if ("jar".equals(url.getProtocol())) {
            // 处理标准jar: URL
            JarURLConnection jarConn = (JarURLConnection) url.openConnection();
            jarConn.setUseCaches(false);
            return jarConn.getJarFile();
        } else {
            // 处理其他类型的URL（如IDE环境）
            String filePath = url.getPath();
            if (filePath.contains("!")) {
                filePath = filePath.substring(0, filePath.indexOf("!"));
            }
            if (filePath.startsWith("file:")) {
                filePath = filePath.substring(5);
            }
            return new JarFile(filePath);
        }
    }
}