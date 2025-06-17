package io.github.tml.mosaic.cube.factory.io.resource;

import io.github.tml.mosaic.util.Assert;
import io.github.tml.mosaic.util.ClassUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 描述: ClassPath下加载资源，getPath()返回绝对路径
 * @author suifeng
 * 日期: 2025/5/29
 */
public class ClassPathResource implements Resource {

    private final String path;
    private final ClassLoader classLoader;
    private volatile String absolutePath; // 缓存绝对路径

    public ClassPathResource(String path) {
        this(path, null);
    }

    public ClassPathResource(String path, ClassLoader classLoader) {
        Assert.notNull(path, "Path 路径不能为空");
        this.path = path;
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URL url = resolveURL();
        if (url == null) {
            throw new FileNotFoundException("资源 '" + path + "' 在classpath中不存在");
        }
        return url.openStream();
    }

    @Override
    public String getPath() {
        if (absolutePath == null) {
            synchronized (this) {
                if (absolutePath == null) {
                    URL url = resolveURL();
                    absolutePath = (url != null) ? url.toString() : generateFallbackPath();
                }
            }
        }
        return absolutePath;
    }

    /**
     * 解析资源的绝对URL
     */
    private URL resolveURL() {
        return classLoader.getResource(path);
    }

    /**
     * 资源不存在时的回退方案
     */
    private String generateFallbackPath() {
        return "classpath:" + path; // 标准回退格式
    }
}