package io.github.tml.mosaic.world.component;

import java.util.Map;

public interface ComponentReplacer {
    void replaceComponent(Map<Class<?>, String> componentNames);
}
