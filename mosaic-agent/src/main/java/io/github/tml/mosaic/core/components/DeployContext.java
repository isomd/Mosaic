package io.github.tml.mosaic.core.components;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
public class DeployContext {
    private final ClassLoader classLoader;
    private final String className;
    private final byte[] originalBytes;
    private byte[] proxyBytes;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public DeployContext(ClassLoader classLoader, String className, byte[] originalBytes) {
        this.classLoader = classLoader;
        this.className = className;
        this.originalBytes = originalBytes;
        this.proxyBytes = originalBytes.clone();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getClassName() {
        return className;
    }

    public byte[] getOriginalBytes() {
        return originalBytes;
    }

    public byte[] getProxyBytes() {
        return proxyBytes;
    }

    public void setProxyBytes(byte[] proxyBytes) {
        this.proxyBytes = proxyBytes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}