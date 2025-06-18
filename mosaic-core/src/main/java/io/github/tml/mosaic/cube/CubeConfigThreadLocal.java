package io.github.tml.mosaic.cube;

import java.util.HashMap;

public class CubeConfigThreadLocal {

    private static final ThreadLocal<HashMap<String, CubeConfig>> cubeConfigThreadLocal = ThreadLocal.withInitial(HashMap::new);

    public static CubeConfig get(String cubeId) {
        return cubeConfigThreadLocal.get().get(cubeId);
    }

    public static void set(String cubeId, CubeConfig cubeConfig) {
        cubeConfigThreadLocal.get().put(cubeId, cubeConfig);
    }
}
