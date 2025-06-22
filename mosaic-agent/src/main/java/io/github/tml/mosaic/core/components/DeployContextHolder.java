package io.github.tml.mosaic.core.components;

import java.util.Map;

public class DeployContextHolder {
    private static final ThreadLocal<Map<String, String>> contextThreadLocal = new ThreadLocal<>();

    public static void set(Map<String, String> params) {
        contextThreadLocal.set(params);
    }

    public static Map<String, String> get() {
        return contextThreadLocal.get();
    }

    public static void clear() {
        contextThreadLocal.remove();
    }
}