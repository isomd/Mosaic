package io.github.tml.mosaic.world.replace;

import java.util.Map;

public interface ReplaceBeanContext {
    void replaceBean(Map<Class<?>, String> componentNames);
}
