package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.support.InfoContext;
import io.github.tml.mosaic.install.support.JarPluginClassLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 从jar包中获取所有类信息
 */
public class JarClassLoaderAllClassCollector implements InfoCollector{
    
    @Override
    public void collect(InfoContext infoContext) {

        Resource resource = infoContext.getResource();
        try {
            try (InputStream inputStream = resource.getInputStream()) {
                 doClassLoad(resource, inputStream, infoContext);
            }
        } catch (IOException e) {
            throw new CubeException("IOException reading Jar Cube from " + resource, e);
        }

    }

    private void doClassLoad(Resource resource, InputStream inputStream, InfoContext infoContext) {
        String jarPath = resource.getPath();
        JarPluginClassLoader classLoader = null;
        try {
            // 1. 创建专用类加载器
            classLoader = createJarClassLoader(jarPath);

            // 2. 扫描Cube定义
            List<Class<?>> allClasses = getAllClassesByJar(inputStream, classLoader);

            infoContext.setClassLoader(classLoader);
            infoContext.setAllClazz(allClasses);

        } catch (Exception e) {
            // 异常时清理资源
            if (classLoader != null) {
                classLoader.close();
            }
            throw new CubeException("安装JAR包失败: " + jarPath, e);
        }
    }

    /**
     * 获取jar的所有class
     * @param jarInputStream
     * @throws IOException
     */
    private List<Class<?>> getAllClassesByJar(InputStream jarInputStream, ClassLoader classLoader) throws IOException {
        ArrayList<Class<?>> classList = new ArrayList<>();
        try (JarInputStream jis = new JarInputStream(jarInputStream)) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (isClassFile(entry)) {
                    String className = extractClassName(entry.getName());
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        classList.add(clazz);
                    } catch (ClassNotFoundException e) {
                        System.out.println("类加载失败: " + className);
                    }
                }
            }
        }
        return classList;
    }

    /**
     * 创建JAR专用类加载器
     */
    private JarPluginClassLoader createJarClassLoader(String jarPath) throws CubeException {
        try {
            File jarFile = new File(jarPath);
            if (!jarFile.exists() || !jarFile.isFile()) {
                throw new CubeException("JAR文件不存在或不是有效文件: " + jarPath);
            }

            URL jarUrl = jarFile.toURI().toURL();
            return new JarPluginClassLoader(
                    jarPath,
                    new URL[]{jarUrl},
                    Thread.currentThread().getContextClassLoader()
            );

        } catch (Exception e) {
            throw new CubeException("创建JAR类加载器失败: " + jarPath, e);
        }
    }

    private boolean isClassFile(JarEntry entry) {
        return !entry.isDirectory() && entry.getName().endsWith(".class");
    }

    private String extractClassName(String entryName) {
        return entryName.replace('/', '.').substring(0, entryName.length() - 6);
    }
}
